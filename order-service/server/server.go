package main

import (
	"context"
	"errors"
	"fmt"
	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
	"gorm.io/gorm"
	"log"
	"net"
	"order-service/database"
	"order-service/models"
	u "order-service/proto/users"
	"regexp"
)

type UserServer struct {
	DB *gorm.DB
	u.UserServiceServer
}

func main() {
	lis, err := net.Listen("tcp", ":8001")

	if err != nil {
		log.Fatalf("Failed to listen: %v", err)
	}

	server := grpc.NewServer()

	db := database.DatabaseConnection()

	u.RegisterUserServiceServer(server, &UserServer{DB: db})
	err = server.Serve(lis)

	if err != nil {
		log.Fatalf("Failed to serve: %v", err)
	}
}

func (userServer *UserServer) Create(_ context.Context, req *u.UserRequest) (*u.UserResponse, error) {
	if req.Name == "" || req.Username == "" || req.Password == "" || req.Address == nil {
		return nil, status.Errorf(codes.InvalidArgument, "Invalid user data")
	}

	zipcodeRegex := regexp.MustCompile("^[1-9]\\d{5}")

	if req.Address.City == "" || req.Address.Street == "" || req.Address.State == "" ||
		req.Address.Locality == "" || zipcodeRegex.Match([]byte(req.Address.Zipcode)) ||
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
		return nil, errors.New(errorString)
	}

	response := &u.UserResponse{
		Name:     user.Name,
		Username: user.Username,
		Address:  req.Address,
	}

	return response, nil
}
