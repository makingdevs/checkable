Rails.application.routes.draw do

  resources :comments
  resources :checkins
  resources :users
  resources :circles
  resources :barista

  resources :users do
    resources :checkins
  end
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html

   get '/login/user/', to: 'users#login', as: 'login'
   post 'checkins/:id/circleFlavor', to: 'circles#create', as: 'circleFlavor'
   post '/users/image/profile', to: 's3_asset#imageProfile', as: 's3'
   get '/checkins/:id/comments', to: 'checkins#comments', as: 'checkinsComments'
   get '/users/photo/:checkin_id', to:'s3_asset#photo_url_s3', as: 'photo_s3'
   post '/checkins/:id/setRating', to: 'checkins#setRatingInCheckin', as: 'saveRating'
   get '/search/users', to:'users#search', as: 'searchUsers'
   get '/foursquare/searh_venues', to: 'foursquare#searh_venues', as: 'api_foursquare_index'
   post '/barista/:id/save', to: 'barista#create', as: 'saveBarista'
end
