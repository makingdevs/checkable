
module Main exposing (..)

-- Elm Core
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (..)
import Http
import Task exposing (Task)
import Json.Decode as Decode exposing ((:=))
import Navigation
import UrlParser exposing (..)
import String

{- Routing -}
-- Ruta deseada: /user/username
type UserProfile
    = MyBarista
    | UserRoute String

matchers : Parser ( UserProfile -> a ) a
matchers =
           oneOf
               {- El orden de los matchers importa -}
               [ format MyBarista ( UrlParser.s "" )
               , format UserRoute ( UrlParser.s "user" </> string ) ]

hashParser : Navigation.Location -> Result String UserProfile
hashParser location =
    location.hash
        |> String.dropLeft 2
        |> parse identity matchers

parser : Navigation.Parser (Result String UserProfile)
parser =
    Navigation.makeParser hashParser

urlUpdate : Result String UserProfile -> Model -> (Model, Cmd Msg)
urlUpdate result model =
    case result of
       Ok route ->
           case route of
               MyBarista ->
                   ( model, Cmd.none )
               UserRoute username ->
                   ( model, fetchUserCmd username)
       Err error ->
           ( model, Cmd.none )

-- MODEL


type alias Checkin =
    { author : String
    , id : Int
    , s3_asset : Maybe CheckinS3Asset
    }

type alias CheckinS3Asset =
    { id : Int
    , url_file : String }

type alias S3Asset =
    { url_file : String }

type alias Model =
  { id : Int
  , name : Maybe String
  , username : String
  , s3_asset : Maybe S3Asset
  , checkins : List Checkin
  , checkins_count : Int
  }

init : Result String UserProfile -> (Model, Cmd Msg)
init result =
    urlUpdate result ( { id = 0
                         , name = Nothing
                         , username = ""
                         , s3_asset = Just { url_file = "http://barist.coffee.s3.amazonaws.com/avatar.png" }
                         , checkins = []
                         , checkins_count = 0 }
                       )

-- UPDATE


-- Base url
api : String
api =
    "http://mybarista.makingdevs.com/"

-- User endpoint
userUrl : String
userUrl =
    api ++ "user/"

-- User command
fetchUserCmd : String -> Cmd Msg
fetchUserCmd username =
    Http.get userDecoder (userUrl ++ username)
        |> Task.perform FetchUserError FetchUserSuccess

-- User decoder
userDecoder : Decode.Decoder Model
userDecoder =
    Decode.object6 Model
        ("id" := Decode.int)
        (Decode.maybe("name" := Decode.string))
        ("username" := Decode.string)
        (Decode.maybe("s3_asset" := s3AssetDecoder))
        ("checkins" := checkinsDecoder)
        ("checkins_count" := Decode.int)

-- Checkin S3Asset decoder
checkinAssetDecoder : Decode.Decoder CheckinS3Asset
checkinAssetDecoder =
    Decode.object2 CheckinS3Asset
        ("id" := Decode.int)
        ("url_file" := Decode.string)

-- S3Asset decoder
s3AssetDecoder : Decode.Decoder S3Asset
s3AssetDecoder =
    Decode.object1 S3Asset
        ("url_file" := Decode.string)

-- Checkin and List Checkin decoder
checkinsDecoder : Decode.Decoder (List Checkin)
checkinsDecoder =
    Decode.list checkinDecoder

checkinDecoder : Decode.Decoder Checkin
checkinDecoder =
    Decode.object3 Checkin
        ("author" := Decode.string)
        ("id" := Decode.int)
        (Decode.maybe("s3_asset" := checkinAssetDecoder))


type Msg
  = FetchUserSuccess Model
    | FetchUserError Http.Error


update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
  case msg of
    FetchUserSuccess user ->
        ( { model | username = user.username
          , s3_asset = user.s3_asset
          , checkins = user.checkins
          , checkins_count = user.checkins_count
          }
        , Cmd.none)
    FetchUserError error ->
        ( {model | username = "User not found :("
          , s3_asset = Nothing
          , checkins = []
          , checkins_count = 0
          }
        , Cmd.none)


-- CHILD VIEWS

-- Navigation
navigation : Html.Html Msg
navigation =
    div[ class "navigation-main row" ]
        [ div [ class "navigation__brand col-xs-6 col-sm-6 col-md-6" ]
              [ div [ class "navigation__brand-container" ]
                    [ img [ src "http://barist.coffee.s3.amazonaws.com/ic_barista_logo.png", class "navigation__image"] []
                    ]
              , div [ class "navigation__text col-sm-4 col-md-4" ]
                    [ h1 []
                          [ text "Barista"]
                    ]
              ]
        , div [ class "navigation__href-session col-xs-6 col-sm-4 col-md-4" ]
              [ ul [ class "navigation__session-items"]
                    [ li [ class "navigation__session-item"] [ a [href "https://play.google.com/store/apps/details?id=makingdevs.com.mybarista"] [text "Descarga la applicación"] ]
                    ]
              ]
        ]

-- Profile
profile : Model -> Html.Html Msg
profile model =
    div [ class "profile-page row" ]
        [ div [ class "profile-page__header"]
              [ div [ class "profile-page__author-container row" ]
                    [ div [ class "profile-page__avatar-container col-xs-4 col-sm-4 col-md-4" ]
                          [ img [ src (model.s3_asset |> Maybe.map .url_file |> Maybe.withDefault "http://barist.coffee.s3.amazonaws.com/avatar.png"), class "profile-page__avatar img-circle" ] []
                          ]
                    , div [ class "profile-page__user-info col-xs-8 col-sm-8 col-md-8" ]
                          [ div [ class "profile-page__username" ]
                                [ p []
                                      [ text model.username ]
                                ]
                          , div [ class "profile-page__statistics" ]
                                [ p []
                                      [ text ( (toString model.checkins_count) ++ " checkins") ]
                                ]
                          ]
                    ]
              ]
        ]

-- Grid
renderCheckin : Checkin -> Html.Html Msg
renderCheckin checkin =
  li [ class "post-grid__item"] [ img [ class "post-grid__item-image", src <| (checkin.s3_asset |> Maybe.map .url_file |> Maybe.withDefault "http://barist.coffee.s3.amazonaws.com/coffee.jpg")] [] ]

renderCheckins : List Checkin -> Html.Html Msg
renderCheckins checkins =
  let
    items = List.map renderCheckin checkins
  in
    ul [ class "post-grid__items"] items

grid : Model -> Html.Html Msg
grid model =
    div [ class "post-grid" ]
        [ div [ class "post-grid__items-container"]
              [ renderCheckins model.checkins ]
        ]

-- Footer
baristUrl : String
baristUrl =
    "http://www.barist.coffee/"
storeUrl : String
storeUrl =
    "https://play.google.com/store/apps/details?id=makingdevs.com.mybarista"
devsUrl : String
devsUrl =
    "http://makingdevs.com"

footer : Html.Html Msg
footer =
    div [ class "footer-main" ]
        [ div [ class "footer-nav" ]
              [ ul [ class "footer__nav-items"]
                    [ li [ class "footer__nav-item" ]
                         [ a [ href baristUrl ]
                             [ text "Barista" ]
                         ]
                    , li [ class "footer__nav-item" ]
                         [ a [ href storeUrl ]
                             [ text "Google Play" ]
                         ]
                    , li [ class "footer__nav-item col" ]
                         [ a [ href devsUrl ]
                             [ text "Making Devs"]
                         ]
                    ]
              ]
        ]

-- VIEW


view : Model -> Html Msg
view model =
  div [ class "shell" ]
      [ div [class "shell__nav"]
            [ navigation
            ]
      , div [class "shell__content"]
            [ profile model
            , grid model
            ]
      , div [class "shell__footer"]
            [ footer
            ]
      ]

-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions model =
  Sub.none


main : Program Never
main =
  Navigation.program parser
    { init = init
    , update = update
    , view = view
    , urlUpdate = urlUpdate
    , subscriptions = subscriptions
    }
