Rails.application.routes.draw do
  resources :checkins
  resources :users
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html

   get '/login/user/', to: 'users#login', as: 'login'

   post '/users/image/profile', to: 'users#imageProfile', as: 's3'
end
