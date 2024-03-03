package main

import (
	"context"
	"github.com/DATA-DOG/go-sqlmock"
	"github.com/stretchr/testify/assert"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	u "order-service/proto/users"
	"reflect"
	"testing"
)

func TestUserServer_Create(t *testing.T) {
	db, mock, err := sqlmock.New()
	assert.Nil(t, err, "Error creating mock db %v", err)

	defer db.Close()

	dialect := postgres.New(postgres.Config{
		Conn:       db,
		DriverName: "postgres",
	})

	gormDb, err := gorm.Open(dialect, &gorm.Config{})
	assert.Nil(t, err, "Error creating mock gorm db %v", err)

	type args struct {
		in0 context.Context
		req *u.UserRequest
	}
	tests := []struct {
		name    string
		args    args
		rows    func()
		want    *u.UserResponse
		wantErr bool
	}{
		{
			name: "Creating a user - Success",
			args: args{
				in0: context.Background(),
				req: &u.UserRequest{
					Name:     "name",
					Username: "user",
					Password: "password",
					Address: &u.Address{
						BuildingNumber: 1,
						Street:         "street",
						Locality:       "locality",
						City:           "city",
						State:          "state",
						Country:        "country",
						Zipcode:        "600001",
					},
				},
			},
			rows: func() {
				mock.ExpectBegin()
				rows := sqlmock.NewRows([]string{
					"id",
					"name",
					"username",
					"password",
					"building_number",
					"street",
					"locality",
					"city",
					"state",
					"country",
					"zipcode",
				}).AddRow(1, "name", "user", "password", 1, "street", "locality", "city", "state", "country", "600001")
				mock.ExpectQuery("INSERT").WillReturnRows(rows)
				mock.ExpectCommit()
			},
			want: &u.UserResponse{
				Name:     "name",
				Username: "user",
				Address: &u.Address{
					BuildingNumber: 1,
					Street:         "street",
					Locality:       "locality",
					City:           "city",
					State:          "state",
					Country:        "country",
					Zipcode:        "600001",
				},
			},
			wantErr: false,
		},
		{
			name: "Creating a user with empty name - Error",
			args: args{
				in0: context.Background(),
				req: &u.UserRequest{
					Name:     "",
					Username: "user",
					Password: "password",
					Address: &u.Address{
						BuildingNumber: 1,
						Street:         "street",
						Locality:       "locality",
						City:           "city",
						State:          "state",
						Country:        "country",
						Zipcode:        "600001",
					},
				},
			},
			rows:    func() {},
			want:    nil,
			wantErr: false,
		},
		{
			name: "Creating a user with empty username - Error",
			args: args{
				in0: context.Background(),
				req: &u.UserRequest{
					Name:     "name",
					Username: "",
					Password: "password",
					Address: &u.Address{
						BuildingNumber: 1,
						Street:         "street",
						Locality:       "locality",
						City:           "city",
						State:          "state",
						Country:        "country",
						Zipcode:        "600001",
					},
				},
			},
			rows:    func() {},
			want:    nil,
			wantErr: false,
		},
		{
			name: "Creating a user with null address - Error",
			args: args{
				in0: context.Background(),
				req: &u.UserRequest{
					Name:     "name",
					Username: "user",
					Password: "password",
					Address:  nil,
				},
			},
			rows:    func() {},
			want:    nil,
			wantErr: false,
		},
		{
			name: "Creating a user with non positive building number - Error",
			args: args{
				in0: context.Background(),
				req: &u.UserRequest{
					Name:     "name",
					Username: "user",
					Password: "password",
					Address: &u.Address{
						BuildingNumber: 0,
						Street:         "street",
						Locality:       "locality",
						City:           "city",
						State:          "state",
						Country:        "country",
						Zipcode:        "600001",
					},
				},
			},
			rows:    func() {},
			want:    nil,
			wantErr: false,
		},
		{
			name: "Creating a user with empty address details - Error",
			args: args{
				in0: context.Background(),
				req: &u.UserRequest{
					Name:     "name",
					Username: "user",
					Password: "password",
					Address: &u.Address{
						BuildingNumber: 0,
						Street:         "",
						Locality:       "locality",
						City:           "city",
						State:          "state",
						Country:        "country",
						Zipcode:        "600001",
					},
				},
			},
			rows:    func() {},
			want:    nil,
			wantErr: false,
		},
		{
			name: "Creating a user with zipcode not matching regex - Error",
			args: args{
				in0: context.Background(),
				req: &u.UserRequest{
					Name:     "name",
					Username: "user",
					Password: "password",
					Address: &u.Address{
						BuildingNumber: 0,
						Street:         "street",
						Locality:       "locality",
						City:           "city",
						State:          "state",
						Country:        "country",
						Zipcode:        "60001",
					},
				},
			},
			rows:    func() {},
			want:    nil,
			wantErr: false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			userServer := &UserServer{
				DB: gormDb,
			}

			tt.rows()
			got, err := userServer.Create(tt.args.in0, tt.args.req)
			if (err != nil) != tt.wantErr {
				statusErr, ok := status.FromError(err)
				assert.True(t, ok, "Expected gRPC status error")
				assert.Equal(t, codes.InvalidArgument, statusErr.Code(), "Expected InvalidArgument error")
				return
			}
			if !reflect.DeepEqual(got, tt.want) {
				t.Errorf("Create() got = %v, want %v", got, tt.want)
			}
		})
	}
}
