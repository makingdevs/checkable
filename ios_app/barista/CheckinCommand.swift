//
//  CheckinCommand.swift
//  barista
//
//  Created by Ariana Santillán on 28/10/16.
//  Copyright © 2016 MakingDevs. All rights reserved.
//

import Foundation

class CheckinCommand {
    
    var username: String?
    var method: String?
    var state: String?
    var origin: String?
    var price: Float?
    var created_at: Date?
    
    init(username: String, method: String, state: String, origin: String, price: Float, created_at: Date) {
        self.username = username
        self.method = method
        self.state = state
        self.origin = origin
        self.price = price
        self.created_at = created_at
    }
}
