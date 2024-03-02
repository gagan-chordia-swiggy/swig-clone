package main

import (
	"context"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	"log"
	u "order-service/proto/users"
)

func main() {
	connection, err := grpc.Dial(":8001", grpc.WithTransportCredentials(insecure.NewCredentials()))

	if err != nil {
		log.Fatalf("Failed to connect: %v", err)
	}

	defer connection.Close()

	client := u.NewUserServiceClient(connection)

	_, err = client.Create(context.Background(), &u.UserRequest{})
}
