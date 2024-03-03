package models

type Order struct {
	Id           uint64  `json:"id" gorm:"primaryKey;autoIncrement:true"`
	RestaurantId string  `json:"restaurant_id"`
	Username     string  `json:"username"`
	Price        float64 `json:"totalAmount"`
	Items        string  `json:"items"`
}
