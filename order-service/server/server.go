package main

import (
	"context"
	"errors"
	"fmt"
	"github.com/google/uuid"
	"google.golang.org/grpc"
	"gorm.io/gorm"
	"log"
	"net"
	"order-service/database"
	"order-service/models"
	u "order-service/proto/users"
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
		Id:       uuid.New().String(),
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
		Id:       user.Id,
		Name:     user.Name,
		Username: user.Username,
		Address:  req.Address,
	}

	return response, nil
}
