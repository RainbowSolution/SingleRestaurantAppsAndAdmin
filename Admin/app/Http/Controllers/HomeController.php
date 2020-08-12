<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\PrivacyPolicy;
use App\Category;
use App\Item;
use App\User;
use App\Order;
use App\Promocode;
use App\Ratting;

class HomeController extends Controller
{
    /**
     * Create a new controller instance.
     *
     * @return void
     */
    public function __construct()
    {
        $this->middleware('auth');
    }

    public function policy() {
        $getprivacypolicy = PrivacyPolicy::where('id', '1')->first();
        return view('privacy-policy', compact('getprivacypolicy'));
    }

    /**
     * Show the application dashboard.
     *
     * @return \Illuminate\Contracts\Support\Renderable
     */
    public function index()
    {
        $getcategory = Category::all();
        $getitems = Item::all();
        $getusers = User::all();
        $getorders = Order::all();
        $getpromocode = Promocode::all();
        $getreview = Ratting::all();
        return view('home',compact('getcategory','getitems','getusers','getorders','getreview','getpromocode'));
    }
}
