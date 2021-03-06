//
//  UserManager.swift
//  barista
//
//  Created by Ariana Santillán on 14/10/16.
//  Copyright © 2016 MakingDevs. All rights reserved.
//

import Foundation
import Alamofire
import SwiftyJSON
import FBSDKLoginKit

class UserManager {
    
    static let sharedInstance = UserManager()
    
    static func signin(loginCommand: LoginCommand, onSuccess:@escaping (_ user: User) -> (), onError:@escaping (_ error: String) -> () ) {
        
        let signInPath: String = "\(Constants.urlBase)/login/user/"
        var signInParams: [String : Any] = [:]
        
        if let email = loginCommand.email, let username = loginCommand.username,
          let firstName = loginCommand.firstName, let lastName = loginCommand.lastName,
          let password = loginCommand.password,
          let token = loginCommand.token {
            signInParams = ["username" : username,
                            "firstName": firstName,
                            "lastName" : lastName,
                            "password" : password,
                            "email"    : email,
                            "token"    : token]
        } else if let username = loginCommand.username, let password = loginCommand.password{
            signInParams = ["username" : username,
                            "password" : password]
        }
        
        Alamofire.request(signInPath, parameters: signInParams)
                 .validate(statusCode: 200..<202)
                 .responseJSON {
            response in
            switch response.result {
            case .success:
                if let value = response.result.value {
                    let json = JSON(value)
                    onSuccess(UserManager.sharedInstance.parseUserJSON(json: json))
                }
            case .failure(_):
                let errorMessage : String
                if let statusCode = response.response?.statusCode {
                    switch(statusCode){
                    case 500,401:
                        errorMessage = "Usuario o contraseña incorrectos"
                    case _:
                        errorMessage = "Desconocido"
                    }
                    onError(errorMessage)
                }
            }
        }
    }
    
    static func signup(registrationCommand: RegistrtionCommand, onSuccess:@escaping (_ user: User) -> (), onError:@escaping (_ error: String) -> () ) {
        
        let signupURL: String = "\(Constants.urlBase)/users/"
        if let email = registrationCommand.email,
            let username = registrationCommand.username,
            let password = registrationCommand.password {
                let parameters = ["email": email,
                          "username": username,
                          "password": password]

                Alamofire.request(signupURL, method: .post, parameters: parameters)
                    .validate(statusCode: 200..<202)
                    .responseJSON {
                        response in
                        switch response.result {
                        case .success:
                            if let value = response.result.value {
                                let json = JSON(value)
                                onSuccess(UserManager.sharedInstance.parseUserJSON(json: json))
                            }
                        case .failure(_):
                            let errorMessage: String
                            if let statusCode = response.response?.statusCode {
                                switch(statusCode) {
                                case 422:
                                    errorMessage = "El usuario ya se encuentra registrado"
                                case _:
                                    errorMessage = "Desconocido"
                                }
                                onError(errorMessage)
                            }
                        }
                }
        }
    }
    
    static func fetchProfile(userId: Int,
                             onSuccess: @escaping (_ userProfile: UserProfile) -> (),
                             onError: @escaping (_ error: String) -> ()) {
        
        let userProfileUrl: String = "\(Constants.urlBase)/users/\(userId)"
        let parameters = ["id": userId]
        
        Alamofire.request(userProfileUrl, parameters: parameters)
            .validate(statusCode: 200..<202)
            .responseJSON {
                response in
                switch response.result {
                case .success:
                    if let value = response.result.value {
                        let json = JSON(value)
                        onSuccess(UserManager.sharedInstance.parseProfileJSON(json: json))
                    }
                case .failure(let error):
                    onError(error.localizedDescription)
                }
        }
    }
    
    static func updateProfile(userCommand: UpdateUserCommand,
                              onSucces: @escaping (_ user: UserProfile) -> (),
                              onError: @escaping (_ error: String) -> ()) {
        
        guard let userId = userCommand.id else { print("there is not user id to update"); return }
        let updateProfileURL: String = "\(Constants.urlBase)/users/\(userId)"
        let parameters = ["id": userId,
                          "name": userCommand.name ?? "",
                          "lastName": userCommand.lastName ?? ""] as [String : Any]
        
        Alamofire.request(updateProfileURL, method: .put, parameters: parameters)
            .validate(statusCode: 200..<202)
            .responseJSON {
                response in
                switch response.result {
                case .success:
                    if let value = response.result.value {
                        let json = JSON(value)
                        onSucces(UserManager.sharedInstance.parseProfileJSON(json: json))
                    }
                case .failure(let error):
                    onError(error.localizedDescription)
                }
        }
    }
    
    func fetchFacebookProfile(onSuccess: @escaping (_ fbProfile: FacebookProfile) -> (),
                              onError: @escaping (_ error: String) -> ()) {
        let profilePath = "/me"
        let readPermissions = ["fields": "id, first_name, last_name, email, birthday"]
        
        FBSDKGraphRequest(graphPath: profilePath, parameters: readPermissions).start {
            (connection, result, error) in
            if let value = result {
                let json = JSON(value)
                let id = json["id"].intValue
                let firstName = json["first_name"].stringValue
                let lastName = json["last_name"].stringValue
                let email = json["email"].stringValue
                let birthday = json["birthday"].stringValue
                let fbProfile = FacebookProfile(id: id, firstName: firstName, lastName: lastName, email: email, birthday: birthday)
                onSuccess(fbProfile)
            } else {
                onError((error?.localizedDescription) ?? "Unknow error")
            }
        }
    }
    
    func parseUserJSON(json: JSON) -> User {
        let userID = json["id"].intValue
        let userName = json["username"].stringValue
        let userPass = json["password_digest"].stringValue
        let user = User(id: userID, username: userName, password: userPass)
        return user
    }
    
    func parseProfileJSON(json: JSON) -> UserProfile {
        let userProfile: UserProfile
        let id = json["id"].intValue
        let username = json["username"].stringValue
        let name = json["name"].stringValue
        let lastName = json["lastName"].stringValue
        let checkinsCount = json["checkins_count"].intValue
        let visibleName = json["visible_name"].stringValue
        if json["s3_asset"].exists() {
            let urlFile = json["s3_asset"]["url_file"].stringValue
            let s3asset = S3Asset(urlFile: urlFile)
            userProfile = UserProfile(id: id,
                                      username: username,
                                      name: name,
                                      lastName: lastName,
                                      checkinsCount: checkinsCount,
                                      visibleName: visibleName,
                                      s3asset: s3asset)
        } else {
            userProfile = UserProfile(id: id,
                                      username: username,
                                      name: name,
                                      lastName: lastName,
                                      checkinsCount: checkinsCount,
                                      visibleName: visibleName)
        }
        return userProfile
    }
}
