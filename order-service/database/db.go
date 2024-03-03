package database

import (
	"errors"
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

func Connection() *gorm.DB {
	connectionString := fmt.Sprintf("host=%s port=%d user=%s password=%s dbname=%s sslmode=%s",
		host, port, username, password, dbName, sslMode)

	db, err := gorm.Open(postgres.Open(connectionString), &gorm.Config{})

	if err != nil {
		log.Fatalf("Error connecting to database: %v\n", err)
	}

	log.Println("Connected to the database")

	err = db.AutoMigrate(&models.User{}, &models.Order{})

	if err != nil {
		log.Fatalf("Error migrating database: %v", err)
	}

	return db
}

func GetUserByUsername(db *gorm.DB, username string) (*models.User, error) {
	var user models.User
	err := db.Where("username = ?", username).Find(&user).Error

	if err != nil {
		errStr := fmt.Sprintf("user not found: %v", err)
		return nil, errors.New(errStr)
	}

	return &user, nil
}
