<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:api')->get('/user', function (Request $request) {
    return $request->user();
});

Route::group(['namespace'=>'Api'],function (){
    Route::post('login','UserController@login');
    Route::post('register','UserController@register');
    Route::post('editprofile','UserController@editprofile');
    Route::post('getprofile','UserController@getprofile');
    Route::post('changepassword','UserController@changepassword');
    Route::post('forgotPassword','UserController@forgotPassword');

    //Driver
    Route::post('driverlogin','UserController@driverlogin');

    Route::get('category','CategoryController@category');

    Route::post('item','ItemController@item');
    Route::post('itemdetails','ItemController@itemdetails');
    Route::post('searchitem','ItemController@searchitem');
    Route::post('addfavorite','ItemController@addfavorite');
    Route::post('favoritelist','ItemController@favoritelist');
    Route::post('removefavorite','ItemController@removefavorite');

    Route::post('cart','CartController@cart');
    Route::post('getcart','CartController@getcart');
    Route::post('qtyupdate','CartController@qtyupdate');
    Route::post('deletecartitem','CartController@deletecartitem');

    Route::post('summary','CheckoutController@summary');
    Route::post('order','CheckoutController@order');
    Route::post('orderhistory','CheckoutController@orderhistory');
    Route::post('getorderdetails','CheckoutController@getorderdetails');
    Route::post('ordercancel','CheckoutController@ordercancel');
    Route::get('promocodelist','CheckoutController@promocodelist');
    Route::post('promocode','CheckoutController@promocode');

    Route::post('ratting','RattingController@ratting');
    Route::get('rattinglist','RattingController@rattinglist');
});