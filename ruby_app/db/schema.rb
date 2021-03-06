# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20161011213938) do

  create_table "barista", force: :cascade, options: "ENGINE=InnoDB DEFAULT CHARSET=latin1" do |t|
    t.string   "name"
    t.datetime "created_at",  null: false
    t.datetime "updated_at",  null: false
    t.integer  "s3_asset_id"
    t.index ["s3_asset_id"], name: "index_barista_on_s3_asset_id", using: :btree
  end

  create_table "checkins", force: :cascade, options: "ENGINE=InnoDB DEFAULT CHARSET=latin1" do |t|
    t.integer  "method",                                   null: false
    t.string   "origin"
    t.decimal  "price",            precision: 8, scale: 2
    t.string   "note"
    t.datetime "created_at",                               null: false
    t.datetime "updated_at",                               null: false
    t.integer  "user_id"
    t.integer  "circle_flavor_id"
    t.string   "rating"
    t.integer  "baristum_id"
    t.integer  "venue_id"
    t.integer  "s3_asset_id"
    t.string   "state"
    t.index ["baristum_id"], name: "index_checkins_on_baristum_id", using: :btree
    t.index ["circle_flavor_id"], name: "index_checkins_on_circle_flavor_id", using: :btree
    t.index ["s3_asset_id"], name: "index_checkins_on_s3_asset_id", using: :btree
    t.index ["user_id"], name: "index_checkins_on_user_id", using: :btree
    t.index ["venue_id"], name: "index_checkins_on_venue_id", using: :btree
  end

  create_table "circle_flavors", force: :cascade, options: "ENGINE=InnoDB DEFAULT CHARSET=latin1" do |t|
    t.integer  "sweetness"
    t.integer  "acidity"
    t.integer  "flowery"
    t.integer  "spicy"
    t.integer  "salty"
    t.integer  "berries"
    t.integer  "chocolate"
    t.integer  "candy"
    t.integer  "body"
    t.integer  "cleaning"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

  create_table "comments", force: :cascade, options: "ENGINE=InnoDB DEFAULT CHARSET=latin1" do |t|
    t.string   "body"
    t.integer  "user_id"
    t.integer  "checkin_id"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["checkin_id"], name: "index_comments_on_checkin_id", using: :btree
    t.index ["user_id"], name: "index_comments_on_user_id", using: :btree
  end

  create_table "s3_assets", force: :cascade, options: "ENGINE=InnoDB DEFAULT CHARSET=latin1" do |t|
    t.string   "url_file"
    t.string   "name_file"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

  create_table "users", force: :cascade, options: "ENGINE=InnoDB DEFAULT CHARSET=latin1" do |t|
    t.string   "username"
    t.string   "token"
    t.datetime "created_at",      null: false
    t.datetime "updated_at",      null: false
    t.string   "password_digest"
    t.string   "name"
    t.string   "lastName"
    t.string   "email"
    t.integer  "s3_asset_id"
    t.index ["s3_asset_id"], name: "index_users_on_s3_asset_id", using: :btree
  end

  create_table "venues", force: :cascade, options: "ENGINE=InnoDB DEFAULT CHARSET=latin1" do |t|
    t.string   "venue_id_foursquare"
    t.string   "name"
    t.string   "formatted_address"
    t.datetime "created_at",          null: false
    t.datetime "updated_at",          null: false
  end

  add_foreign_key "barista", "s3_assets"
  add_foreign_key "checkins", "barista"
  add_foreign_key "checkins", "circle_flavors"
  add_foreign_key "checkins", "s3_assets"
  add_foreign_key "checkins", "users"
  add_foreign_key "checkins", "venues"
  add_foreign_key "comments", "checkins"
  add_foreign_key "comments", "users"
  add_foreign_key "users", "s3_assets"
end
