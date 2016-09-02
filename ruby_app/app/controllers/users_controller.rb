require 'securerandom'

class UsersController < ApplicationController
  before_action :set_user, only: [:show, :update, :destroy]

  # GET /users
  def index
    @users = User.all

    render json: @users
  end

  # GET /users/1
  def show
    render json: @user
  end

  # GET /user/username
  def showUser
    @user = User.find_by username:params['username']
    render json: @user
  end

  # POST /users
  def create
    @user = User.new(user_params)
    @user.token = Digest::MD5.hexdigest(params[:username]+params[:password])
    if @user.save
      render json: @user, status: :created, location: @user
    else
      render json: @user.errors, status: :unprocessable_entity
    end
  end

  # PATCH/PUT /users/1
  def update
    if @user.update(user_params)
      render json: @user
    else
      render json: @user.errors, status: :unprocessable_entity
    end
  end

  # GET /users/login   => username, password =>success 200 o 201 y user token , error 401
  def login
    @user = User.find_by username:params['username']
    if @user.authenticate(params['password'])
      render json: @user
    else
      render :json => {:error => "Unauthorized"}.to_json, :status => 401
    end
  end

  # DELETE /users/1
  def destroy
    @user.destroy
  end

  def search
    @users = User.search(params[:search])
    render json: @users
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_user
      @user = User.find(params[:id])
    end

    # Only allow a trusted parameter "white list" through.
    def user_params
      params.permit(:username,:name,:lastName,:password,:email)
    end
end
