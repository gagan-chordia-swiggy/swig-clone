package models

type Address struct {
	BuildingNumber uint32
	Street         string
	Locality       string
	City           string
	State          string
	Country        string
	Zipcode        string
}

type User struct {
	Id       string   `gorm:"primaryKey" json:"id"`
	Name     string   `json:"name"`
	Username string   `json:"username" gorm:"unique"`
	Password string   `json:"password"`
	Address  *Address `json:"address" gorm:"embedded"`
}
