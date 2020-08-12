<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class OrderDetails extends Model
{
    protected $table='order_details';
    protected $fillable=['user_id','order_id','item_id','price','qty'];

    public function itemimage(){
        return $this->hasOne('App\ItemImages','item_id','id')->select('item_images.id','item_images.item_id',\DB::raw("CONCAT('".url('/public/images/item/')."/', item_images.image) AS image"));
    }

    public function items(){
        return $this->hasOne('App\Item','id','item_id');
    }
}
