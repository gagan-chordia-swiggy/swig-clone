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
	Id       uint64   `gorm:"primaryKey;autoIncrement:true" json:"id"`
	Name     string   `json:"name"`
	Username string   `json:"username" gorm:"unique"`
	Password string   `json:"password"`
	Address  *Address `json:"address" gorm:"embedded"`
}
