class Checkin < ApplicationRecord
  enum method: [:Expresso,:Americano,:Goteo,:Prensa,:Sifón,:Otro ]
  has_many :comments
  belongs_to :user
  belongs_to :circle_flavor, optional: true
  has_one :s3_asset
  belongs_to :baristum, optional: true
  def as_json(options={})
    super(
        :include => {
        :s3_asset => {:only => [:url_file]},
        :baristum => {:only => [:name]}
      }
    )
  end

end
