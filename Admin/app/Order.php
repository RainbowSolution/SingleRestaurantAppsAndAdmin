<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Order extends Model
{
    protected $table='order';
    protected $fillable=['user_id','order_total','razorpay_payment_id','payment_type','address','promocode'];

    public function users(){
        return $this->hasOne('App\User','id','user_id');
    }
}
