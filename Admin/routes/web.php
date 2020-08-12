<?php

use Illuminate\Support\Facades\Route;
use Illuminate\Database\Connection;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
	return redirect('admin');
});

Route::get('privacy-policy', 'HomeController@policy');

Route::post('adminregister', 'HomeController@AdminRegister');

Route::group(['prefix' => 'admin', 'namespace' => 'admin'], function () {
	// Route::get('/', function () {
		
	// });

	Route::get('/', function () {

	    // Test database connection
	    try {
	        DB::connection()->getPdo();
	        return view('auth.login');
	    } catch (\Exception $e) {
	        $https = false;
	        if (isset($_SERVER['HTTPS']) && ($_SERVER['HTTPS'] == 'on' || $_SERVER['HTTPS'] == 1) || isset($_SERVER['HTTP_X_FORWARDED_PROTO']) && $_SERVER['HTTP_X_FORWARDED_PROTO'] == 'https') {
	        	$protocol = 'https://';
	        } else {
	        	$protocol = 'http://';
	        }
	        $dirname = rtrim(dirname($_SERVER['PHP_SELF']), '/') . '/';
	        if (isset($_SERVER['HTTPS']) and $_SERVER['HTTPS'] != 'off') {
	        	$https = true;
	        }

	        $installerurl = $protocol . $_SERVER['HTTP_HOST'] . $dirname;
	        header('Location: ' . $installerurl . 'install');
	        exit;
	    }
	});

	Route::group(['middleware' => ['AdminAuth']],function(){
		Route::get('home', 'AdminController@home');
		Route::post('changePassword', 'AdminController@changePassword');
		Route::post('settings', 'AdminController@settings');

		Route::get('category', 'CategoryController@index');
		Route::post('category/store', 'CategoryController@store');
		Route::get('category/list', 'CategoryController@list');
		Route::post('category/show', 'CategoryController@show');
		Route::post('category/update', 'CategoryController@update');
		Route::post('category/destroy', 'CategoryController@destroy');

		Route::get('item', 'ItemController@index');
		Route::post('item/store', 'ItemController@store');
		Route::get('item/list', 'ItemController@list');
		Route::post('item/show', 'ItemController@show');
		Route::post('item/update', 'ItemController@update');
		Route::post('item/destroy', 'ItemController@destroy');
		Route::get('item-images/{id}', 'ItemController@itemimages');
		Route::post('item/showimage', 'ItemController@showimage');
		Route::post('item/updateimage', 'ItemController@updateimage');
		Route::get('item/itemimages', 'ItemController@itemimages');
		Route::post('item/storeimages', 'ItemController@storeimages');
		Route::post('item/storeingredientsimages', 'ItemController@storeingredientsimages');
		Route::post('item/destroyimage', 'ItemController@destroyimage');
		Route::post('item/destroyingredients', 'ItemController@destroyingredients');
		Route::post('item/updateingredients', 'ItemController@updateingredients');
		Route::post('item/showingredients', 'ItemController@showingredients');

		Route::get('users', 'UserController@index');
		Route::post('users/store', 'UserController@store');
		Route::get('users/list', 'UserController@list');
		Route::post('users/show', 'UserController@show');
		Route::post('users/update', 'UserController@update');
		Route::post('users/destroy', 'UserController@destroy');

		Route::get('orders', 'OrderController@index');
		Route::get('orders/list', 'OrderController@list');
		Route::get('invoice/{id}', 'OrderController@invoice');
		Route::post('orders/destroy', 'OrderController@destroy');
		Route::post('orders/update', 'OrderController@update');

		Route::get('reviews', 'RattingController@index');
		Route::get('reviews/list', 'RattingController@list');
		Route::post('reviews/destroy', 'RattingController@destroy');

		Route::get('promocode', 'PromocodeController@index');
		Route::post('promocode/store', 'PromocodeController@store');
		Route::get('promocode/list', 'PromocodeController@list');
		Route::post('promocode/show', 'PromocodeController@show');
		Route::post('promocode/update', 'PromocodeController@update');
		Route::post('promocode/destroy', 'PromocodeController@destroy');

		Route::get('privacypolicy', 'PrivacyPolicyController@index');
		Route::post('privacypolicy/update', 'PrivacyPolicyController@update');
		
	});

	Route::get('logout', 'AdminController@logout');
});

Auth::routes();

Route::get('/home', 'HomeController@index')->name('home');

