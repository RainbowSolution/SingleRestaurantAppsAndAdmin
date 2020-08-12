<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Ratting extends Model
{
    protected $table='ratting';
    protected $fillable=['user_id','ratting','comment'];

    public function users(){
        return $this->hasOne('App\User','id','user_id');
    }
}