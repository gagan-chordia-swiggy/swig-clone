package main

import (
	"context"
	"encoding/base64"
	"github.com/DATA-DOG/go-sqlmock"
	"github.com/stretchr/testify/assert"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/metadata"
	"google.golang.org/grpc/status"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	o "order-service/proto/orders"
	u "order-service/proto/users"
	"reflect"
	"regexp"
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

func TestOrderServer_Place(t *testing.T) {
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
		ctx context.Context
		req *o.OrderRequest
	}
	type errs struct {
		required   bool
		statusCode codes.Code
	}

	tests := []struct {
		name    string
		args    args
		rows    func()
		want    *o.OrderResponse
		wantErr errs
	}{
		{
			name: "Place an order - without authentication",
			args: args{
				ctx: context.Background(),
				req: &o.OrderRequest{
					RestaurantId: "52f5e71b-3082-49b0-aec0-c31566c8b827",
					Items: map[string]uint32{
						"Dosa": 2,
						"Idli": 2,
					},
				},
			},
			rows: func() {},
			want: nil,
			wantErr: errs{
				required:   true,
				statusCode: codes.Unauthenticated,
			},
		},
		{
			name: "Place an order - user not found",
			args: args{
				ctx: mockContext(),
				req: &o.OrderRequest{
					RestaurantId: "52f5e71b-3082-49b0-aec0-c31566c8b827",
					Items: map[string]uint32{
						"Dosa": 2,
						"Idli": 2,
					},
				},
			},
			rows: func() {
				mock.ExpectQuery(regexp.QuoteMeta(`SELECT * FROM "users" WHERE username = $1`)).WithArgs("gagan").WillReturnError(gorm.ErrRecordNotFound)
			},
			want: nil,
			wantErr: errs{
				required:   true,
				statusCode: codes.NotFound,
			},
		},
		{
			name: "Place an order - with invalid credentials",
			args: args{
				ctx: mockContext(),
				req: &o.OrderRequest{
					RestaurantId: "52f5e71b-3082-49b0-aec0-c31566c8b827",
					Items: map[string]uint32{
						"Dosa": 2,
						"Idli": 2,
					},
				},
			},
			rows: func() {
				rows := sqlmock.NewRows([]string{"id", "name", "username", "password", "building_number", "street", "locality", "city", "state", "country", "zipcode"}).
					AddRow(5, "Gagan", "gagan", "passwor", 14, "Ramanuja Street", "Sowcarpet", "Chennai", "Tamil Nadu", "India", "600001")
				//mock.ExpectBegin()
				mock.ExpectQuery(regexp.QuoteMeta(`SELECT * FROM "users" WHERE username = $1`)).WithArgs("gagan").WillReturnRows(rows)
				//mock.ExpectCommit()
			},
			want: nil,
			wantErr: errs{
				required:   true,
				statusCode: codes.InvalidArgument,
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			orderServer := &OrderServer{
				DB: gormDb,
			}
			tt.rows()
			got, err := orderServer.Place(tt.args.ctx, tt.args.req)
			if (err != nil) == tt.wantErr.required {
				statusErr, ok := status.FromError(err)
				assert.True(t, ok, "Expected gRPC status error")
				assert.Equal(t, tt.wantErr.statusCode, statusErr.Code())
				return
			}
			assert.Equalf(t, tt.want, got, "Place(%v, %v)", tt.args.ctx, tt.args.req)
		})
	}
}

func mockContext() context.Context {
	creds := "gagan:password"
	encodedCreds := base64.StdEncoding.EncodeToString([]byte(creds))

	authHeader := "Basic " + encodedCreds

	md := metadata.New(map[string]string{
		"authorization": authHeader,
	})

	return metadata.NewIncomingContext(context.Background(), md)
}
