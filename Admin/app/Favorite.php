<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Favorite extends Model
{
    protected $table='favorite';
    protected $fillable=['user_id','item_id'];

    public function itemimage(){
        return $this->hasOne('App\ItemImages','item_id','id')->select('id','item_id',\DB::raw("CONCAT('".url('/public/images/item/')."/', image) AS image"));
    }
}