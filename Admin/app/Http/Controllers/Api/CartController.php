<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Cart;
use App\Item;
use App\ItemImages;
use Illuminate\Support\Facades\DB;
use Validator;

class CartController extends Controller
{
    public function cart(Request $request)
    {
      if($request->item_id == ""){
          return response()->json(["status"=>0,"message"=>"Item is required"],400);
      }
      if($request->qty == ""){
          return response()->json(["status"=>0,"message"=>"Qty is required"],400);
      }
      if($request->price == ""){
          return response()->json(["status"=>0,"message"=>"Price is required"],400);
      }
      if($request->user_id == ""){
          return response()->json(["status"=>0,"message"=>"User ID is required"],400);
      }

      $data=Cart::where('cart.user_id',$request['user_id'])
      ->where('cart.item_id', $request['item_id'])
      ->get()
	    ->first();

  		try {
  		    if($data!="") {
  		    	$result = DB::table('cart')
  		    	    ->where('cart.user_id',$request['user_id'])
  		    	    ->where('cart.item_id', $request['item_id'])
  		    	    ->update([
  		    	        'qty' => $data->qty+'1',
  		    	        'price' => $request->price*($data->qty+'1'),
  		    	    ]);
  	            return response()->json(['status'=>1,'message'=>'Qty has been update'],200);
  	        } else {
  	            $cart = new Cart;
  	            $cart->item_id =$request->item_id;
  	            $cart->qty =$request->qty;
  	            $cart->price =$request->price;
  	            $cart->user_id =$request->user_id;
  	            $cart->save();

  	            return response()->json(['status'=>1,'message'=>'Item has been added to your cart'],200);
  	        }

  		} catch (\Exception $e){

  		    return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
  		}
   	}

   	public function getcart(Request $request)
   	{
   	    if($request->user_id == ""){
   	        return response()->json(["status"=>0,"message"=>"User ID is required"],400);
   	    }

   	    $cartdata=Cart::with('itemimage')->select('cart.id','cart.qty','cart.price','item.item_name','cart.item_id')
   	    ->join('item','cart.item_id','=','item.id')
   	    ->where('cart.user_id',$request->user_id)->get();
   	    if(!empty($cartdata))
   	    {
   	        return response()->json(['status'=>1,'message'=>'Cart Data Successful','data'=>$cartdata],200);
   	    }
   	    else
   	    {
   	        return response()->json(['status'=>0,'message'=>'No data found'],200);
   	    }
   	}

   	public function qtyupdate(Request $request)
   	{
   	    if($request->cart_id == ""){
   	        return response()->json(["status"=>0,"message"=>"Cart ID is required"],400);
   	    }
   	    if($request->item_id == ""){
   	        return response()->json(["status"=>0,"message"=>"Item is required"],400);
   	    }
   	    if($request->qty == ""){
   	        return response()->json(["status"=>0,"message"=>"Qty is required"],400);
   	    }
   	    if($request->user_id == ""){
   	        return response()->json(["status"=>0,"message"=>"User ID is required"],400);
   	    }

        $data=Item::where('item.id', $request['item_id'])
        ->get()
        ->first();
        $cart = new Cart;
        $cart->exists = true;
        $cart->id = $request->cart_id;
        $cart->item_id =$request->item_id;
  	    $cart->qty =$request->qty;
  	    $cart->price = $data->item_price*($request->qty);
  	    $cart->user_id =$request->user_id;
        $cart->save();

        return response()->json(['status'=>1,'message'=>'Qty has been update'],200);
   	}

    public function deletecartitem(Request $request)
    {
        if($request->cart_id == ""){
            return response()->json(["status"=>0,"message"=>"Cart data is required"],400);
        }

        $cart=Cart::where('id', $request->cart_id)->delete();
        if($cart)
        {
            return response()->json(['status'=>1,'message'=>'Item has been deleted'],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'Somethig went wrong'],200);
        }
    }
}