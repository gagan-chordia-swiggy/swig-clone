package database

import (
	"fmt"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"log"
	"order-service/models"
)

const (
	host     = "localhost"
	port     = 5432
	username = "postgres"
	password = "root1234"
	dbName   = "order_service"
	sslMode  = "disable"
)

func DatabaseConnection() *gorm.DB {
	connectionString := fmt.Sprintf("host=%s port=%d user=%s password=%s dbname=%s sslmode=%s",
		host, port, username, password, dbName, sslMode)

	db, err := gorm.Open(postgres.Open(connectionString), &gorm.Config{})

	if err != nil {
		log.Fatalf("Error connecting to database: %v\n", err)
	}

	log.Println("Connected to the database")

	err = db.AutoMigrate(&models.User{})

	if err != nil {
		log.Fatalf("Error migrating database: %v", err)
	}

	return db
}
