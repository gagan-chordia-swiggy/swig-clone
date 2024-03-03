package main

import (
	"context"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/metadata"
	"google.golang.org/grpc/status"
	"gorm.io/gorm"
	"io/ioutil"
	"log"
	"net"
	"net/http"
	"order-service/database"
	"order-service/models"
	o "order-service/proto/orders"
	u "order-service/proto/users"
	"reflect"
	"strings"
)

type UserServer struct {
	DB *gorm.DB
	u.UserServiceServer
}

type OrderServer struct {
	DB *gorm.DB
	o.OrderServiceServer
}

func main() {
	go createOrderServer()
	createUserServer()
}

func createOrderServer() {
	lis2, err := net.Listen("tcp", ":8002")
	if err != nil {
		log.Fatalf("Failed to listen: 8002, %v", err)
	}

	oServer := grpc.NewServer()
	db := database.Connection()

	o.RegisterOrderServiceServer(oServer, &OrderServer{DB: db})
	err = oServer.Serve(lis2)
	if err != nil {
		log.Fatalf("Failed to serve 8002: %v", err)
	}
}

func createUserServer() {
	lis1, err := net.Listen("tcp", ":8001")
	if err != nil {
		log.Fatalf("Failed to listen: 8001, %v", err)
	}

	uServer := grpc.NewServer()
	db := database.Connection()

	u.RegisterUserServiceServer(uServer, &UserServer{DB: db})
	err = uServer.Serve(lis1)
	if err != nil {
		log.Fatalf("Failed to serve 8001: %v", err)
	}
}

func (userServer *UserServer) Create(_ context.Context, req *u.UserRequest) (*u.UserResponse, error) {
	if req.Name == "" || req.Username == "" || req.Password == "" || req.Address == nil {
		return nil, status.Errorf(codes.InvalidArgument, "Invalid user data")
	}

	if req.Address.City == "" || req.Address.Street == "" || req.Address.State == "" ||
		req.Address.Locality == "" || req.Address.Zipcode == "" ||
		req.Address.Country == "" || req.Address.BuildingNumber < 1 {
		return nil, status.Errorf(codes.InvalidArgument, "Invalid address data")
	}

	address := &models.Address{
		BuildingNumber: req.Address.BuildingNumber,
		Street:         req.Address.Street,
		Locality:       req.Address.Locality,
		City:           req.Address.City,
		State:          req.Address.State,
		Country:        req.Address.Country,
		Zipcode:        req.Address.Zipcode,
	}
	user := &models.User{
		Name:     req.Name,
		Username: req.Username,
		Password: req.Password,
		Address:  address,
	}

	err := userServer.DB.Create(&user).Error

	if err != nil {
		errorString := fmt.Sprintf("error storing the user: %v", err)
		return nil, status.Errorf(codes.Unknown, errorString)
	}

	response := &u.UserResponse{
		Name:     user.Name,
		Username: user.Username,
		Address:  req.Address,
	}

	return response, nil
}

func (orderServer *OrderServer) Place(ctx context.Context, req *o.OrderRequest) (*o.OrderResponse, error) {
	username, password, ok := extractCredentials(ctx)

	if !ok {
		return nil, status.Errorf(codes.Unauthenticated, "Credentials are missing")
	}

	user, err := database.GetUserByUsername(orderServer.DB, username)

	if err != nil {
		return nil, status.Errorf(codes.NotFound, err.Error())
	}

	if !reflect.DeepEqual(user.Password, password) {
		return nil, status.Errorf(codes.InvalidArgument, "Invalid Credentials")
	}

	total, err := calculateTotal(req)
	itemsByte, err := json.Marshal(req.Items)
	itemStr := string(itemsByte)

	order := &models.Order{
		Username:     username,
		RestaurantId: req.RestaurantId,
		Items:        itemStr,
		Price:        total,
	}

	err = orderServer.DB.Create(&order).Error

	if err != nil {
		errorString := fmt.Sprintf("error storing the order: %v", err)
		return nil, status.Errorf(codes.Unknown, errorString)
	}

	response := &o.OrderResponse{
		OrderId:      order.Id,
		Username:     username,
		RestaurantId: req.RestaurantId,
		Items:        req.Items,
		TotalAmount:  total,
	}

	return response, nil
}

func extractCredentials(ctx context.Context) (string, string, bool) {
	md, ok := metadata.FromIncomingContext(ctx)
	if !ok {
		return "", "", false
	}

	authHeaders, ok := md["authorization"]

	if !ok || len(authHeaders) == 0 {
		return "", "", false
	}

	authHeader := authHeaders[0]
	if !strings.HasPrefix(authHeader, "Basic ") {
		return "", "", false
	}

	decoded, err := base64.StdEncoding.DecodeString(authHeader[6:])
	if err != nil {
		return "", "", false
	}

	credentials := strings.SplitN(string(decoded), ":", 2)
	if len(credentials) != 2 {
		return "", "", false
	}

	return credentials[0], credentials[1], true
}

func calculateTotal(r *o.OrderRequest) (float64, error) {
	rId := r.RestaurantId
	total := 0.0

	for k, v := range r.Items {
		apiStr := fmt.Sprintf("http://localhost:8080/api/v1/restaurants/%v/items/%v", rId, k)
		resp, err := http.Get(apiStr)
		if err != nil {
			return 0.0, err
		}

		bodyBytes, _ := ioutil.ReadAll(resp.Body)
		body := string(bodyBytes)

		var response struct {
			Data struct {
				Items struct {
					Price float64 `json:"price"`
				} `json:"items"`
			} `json:"data"`
		}

		if err := json.Unmarshal([]byte(body), &response); err != nil {
			return 0.0, err
		}

		price := response.Data.Items.Price
		total += price * float64(v)
	}

	return total, nil
}
