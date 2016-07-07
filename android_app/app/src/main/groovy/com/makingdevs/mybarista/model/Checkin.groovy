package  com.makingdevs.mybarista.model

import groovy.beans.Bindable
import groovy.transform.CompileStatic

@CompileStatic
class Checkin {

    Date created_at
    String id
    String method
    String note
    String origin
    String price

    Date updated_at
    String user_id
    String circle_flavor_id
    String rating
    @Bindable S3Asset s3_asset
    Barista baristum = new Barista()
    String author
    
}