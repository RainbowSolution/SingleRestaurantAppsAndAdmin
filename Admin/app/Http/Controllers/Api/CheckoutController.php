<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Cart;
use App\Order;
use App\User;
use App\OrderDetails;
use App\Promocode;
use Illuminate\Support\Facades\DB;
use Validator;

class CheckoutController extends Controller
{
    public function summary(Request $request)
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User ID is required"],400);
        }

        $cartdata=Cart::select('cart.id','cart.qty','cart.price as total_price','item.item_name','item.item_price','cart.item_id')
        ->join('item','cart.item_id','=','item.id')
        ->where('cart.user_id',$request->user_id)->get()->toArray();

        $taxval=User::select('users.tax')->where('users.id','1')
        ->get()->first();

        foreach ($cartdata as $value) {
        	$data[] = array(
        	    "id" => $value['id'],
        	    "qty" => $value['qty'],
        	    "total_price" => $value['total_price'],
        	    "item_name" => $value['item_name'],
        	    "item_price" => $value['item_price'],
        	    "item_id" => $value['item_id']
        	);
        }
        @$order_total = array_sum(array_column(@$data, 'total_price'));
        $summery = array(
        	'order_total' => "$order_total",
        	'tax' => "$taxval->tax", 
        	'delivery_charge' => "100", 
        );
        
        if(!empty($cartdata))
        {
            return response()->json(['status'=>1,'message'=>'Summery list Successful','data'=>@$data,'summery'=>$summery],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'No data found'],200);
        }
    }

    public function order(Request $request)
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User ID is required"],400);
        }
        if($request->order_total == ""){
            return response()->json(["status"=>0,"message"=>"Total Amount is required"],400);
        }
        
        if($request->payment_type == ""){
            return response()->json(["status"=>0,"message"=>"Payment Type is required"],400);
        }
        if($request->address == ""){
            return response()->json(["status"=>0,"message"=>"Address is required"],400);
        }

        $order_number = substr(str_shuffle(str_repeat("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", 10)), 0, 10);

    	try {

    	    if($request->payment_type == "1") {
    	    	$order = new Order;
    	    	$order->order_number =$order_number;
    	    	$order->user_id =$request->user_id;
    	    	$order->order_total =$request->order_total;
    	    	$order->razorpay_payment_id =$request->razorpay_payment_id;
    	    	$order->payment_type =$request->payment_type;
                $order->status ='1';
    	    	$order->address =$request->address;
                $order->promocode =$request->promocode;
                $order->discount_amount =$request->discount_amount;
                $order->discount_pr =$request->discount_pr;
                $order->tax =$request->tax;
                $order->tax_amount =$request->tax_amount;
                $order->delivery_charge =$request->delivery_charge;
    	    	$order->save();

    	    	$order_id = DB::getPdo()->lastInsertId();
    	    	$data=Cart::where('cart.user_id',$request['user_id'])
    	    	->get();

    	    	foreach ($data as $value) {
    	    	    $OrderPro = new OrderDetails;
    	    	    $OrderPro->order_id = $order_id;
                    $OrderPro->user_id = $value['user_id'];
    	    	    $OrderPro->item_id = $value['item_id'];
    	    	    $OrderPro->price = $value['price'];
    	    	    $OrderPro->qty = $value['qty'];
    	    	    $OrderPro->save();
    	    	}
    	    	$cart=Cart::where('user_id', $request->user_id)->delete();


                //Notification
                $getalluses=User::select('users.token')->where('users.id',$request->user_id)
                ->get()->first();

                // dd($getalluses->token);

                $title = "Order";
                $body = "Order has been placed";
                $google_api_key = "AAAABdWnJCg:APA91bHZIrdC8Vre9lRlW89DNVYlwtvafqMf2yfgS8sdNMcnT7q1xJnVmCKu3vAP51QsrVwKzAzjk_7dZ3UC1Nc7J0-gf8CMjiaHseoUcwdp7WpS-Bl6l__NhGOrfn975IMPT7gPpPLL"; 
                
                $registrationIds = $getalluses->token;
                #prep the bundle
                $msg = array
                    (
                    'body'  => $body,
                    'title' => $title,
                    'sound' => 1/*Default sound*/
                    );
                $fields = array
                    (
                    'to'            => $registrationIds,
                    'notification'  => $msg
                    );
                $headers = array
                    (
                    'Authorization: key=' . $google_api_key,
                    'Content-Type: application/json'
                    );
                #Send Reponse To FireBase Server
                $ch = curl_init();
                curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
                curl_setopt( $ch,CURLOPT_POST, true );
                curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
                curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
                curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
                curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );

                $result = curl_exec ( $ch );
                // dd($result);
                curl_close ( $ch );

    	    	return response()->json(['status'=>1,'message'=>'Order has been placed'],200);
            } else {

                $order = new Order;
                $order->order_number =$order_number;
    	    	$order->user_id =$request->user_id;
    	    	$order->order_total =$request->order_total;
    	    	$order->payment_type =$request->payment_type;
                $order->status ='1';
    	    	$order->address =$request->address;
                $order->promocode =$request->promocode;
                $order->discount_amount =$request->discount_amount;
                $order->discount_pr =$request->discount_pr;
                $order->tax =$request->tax;
                $order->tax_amount =$request->tax_amount;
                $order->delivery_charge =$request->delivery_charge;
    	    	$order->save();

    	    	$order_id = DB::getPdo()->lastInsertId();
    	    	$data=Cart::where('cart.user_id',$request['user_id'])
    	    	->get();
    	    	foreach ($data as $value) {
    	    	    $OrderPro = new OrderDetails;
    	    	    $OrderPro->order_id = $order_id;
                    $OrderPro->user_id = $value['user_id'];
    	    	    $OrderPro->item_id = $value['item_id'];
    	    	    $OrderPro->price = $value['price'];
    	    	    $OrderPro->qty = $value['qty'];
    	    	    $OrderPro->save();
    	    	}
    	    	$cart=Cart::where('user_id', $request->user_id)->delete();

                //Notification
                $getalluses=User::select('users.token')->where('users.id',$request->user_id)
                ->get()->first();

                // dd($getalluses->token);

                $title = "Order";
                $body = "Order has been placed";
                $google_api_key = "AAAABdWnJCg:APA91bHZIrdC8Vre9lRlW89DNVYlwtvafqMf2yfgS8sdNMcnT7q1xJnVmCKu3vAP51QsrVwKzAzjk_7dZ3UC1Nc7J0-gf8CMjiaHseoUcwdp7WpS-Bl6l__NhGOrfn975IMPT7gPpPLL"; 
                
                $registrationIds = $getalluses->token;
                #prep the bundle
                $msg = array
                    (
                    'body'  => $body,
                    'title' => $title,
                    'sound' => 1/*Default sound*/
                    );
                $fields = array
                    (
                    'to'            => $registrationIds,
                    'notification'  => $msg
                    );
                $headers = array
                    (
                    'Authorization: key=' . $google_api_key,
                    'Content-Type: application/json'
                    );
                #Send Reponse To FireBase Server
                $ch = curl_init();
                curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
                curl_setopt( $ch,CURLOPT_POST, true );
                curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
                curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
                curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
                curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );

                $result = curl_exec ( $ch );
                // dd($result);
                curl_close ( $ch );

                return response()->json(['status'=>1,'message'=>'Order has been placed'],200);
            }

    	} catch (\Exception $e){

    	    return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
    	}
    }

    public function orderhistory(Request $request)
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User ID is required"],400);
        }

        $cartdata=OrderDetails::select('order.order_total as total_price',DB::raw('SUM(order_details.qty) AS qty'),'order.id','order.order_number','order.status','order.payment_type')
        ->join('item','order_details.item_id','=','item.id')
        ->join('order','order_details.order_id','=','order.id')
        ->where('order.user_id',$request->user_id)->groupBy('order_details.order_id')->get();

        
        
        if(!empty($cartdata))
        {
            return response()->json(['status'=>1,'message'=>'Order history list Successful','data'=>$cartdata],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'No data found'],200);
        }
    }

    public function getorderdetails(Request $request)
    {
        if($request->order_id == ""){
            return response()->json(["status"=>0,"message"=>"Order Number is required"],400);
        }

        $cartdata=OrderDetails::with('itemimage')->select('order_details.id','order_details.qty','order_details.price as total_price','item.id','item.item_name','item.item_price','order_details.item_id')
        ->join('item','order_details.item_id','=','item.id')
        ->join('order','order_details.order_id','=','order.id')
        ->where('order_details.order_id',$request->order_id)->get()->toArray();
        
        $status=Order::select('order.address','order.promocode','order.discount_amount','order.order_number','order.status')->where('order.id',$request['order_id'])
        ->get()->first();

        $taxval=User::select('users.tax')->where('users.id','1')
        ->get()->first();

        foreach ($cartdata as $value) {
            $data[] = array(
                "total_price" => $value['total_price']
            );
        }
        @$order_total = array_sum(array_column(@$data, 'total_price'));
        $summery = array(
            'order_total' => "$order_total",
            'tax' => "$taxval->tax",
            'discount_amount' => $status->discount_amount,
            'promocode' => $status->promocode,
            'delivery_charge' => "100",
        );
        
        if(!empty($cartdata))
        {
            return response()->json(['status'=>1,'message'=>'Summery list Successful','address'=>$status->address,'order_number'=>$status->order_number,'data'=>@$cartdata,'summery'=>$summery],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'No data found'],200);
        }
    }

    public function ordercancel(Request $request)
    {
        if($request->order_id == ""){
            return response()->json(["status"=>0,"message"=>"Order Number is required"],400);
        }
        
        $UpdateDetails = Order::where('id', $request->order_id)
                    ->update(['status' => '4']);
        
        if(!empty($UpdateDetails))
        {
            return response()->json(['status'=>1,'message'=>'Order has been cancelled'],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
        }
    }

    public function promocodelist()
    {
        
        $promocode=Promocode::select('promocode.offer_name','promocode.offer_code','promocode.offer_amount','promocode.description')
        ->get();

        if(!empty($promocode))
        {
            return response()->json(['status'=>1,'message'=>'Promocode List','data'=>$promocode],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'No Promocode found'],200);
        }
    }

    public function promocode(Request $request)
    {
        if($request->offer_code == ""){
            return response()->json(["status"=>0,"message"=>"Promocode is required"],400);
        }

        $promocode=Promocode::select('promocode.offer_amount','promocode.description','promocode.offer_code')->where('promocode.offer_code',$request['offer_code'])
        ->get()->first();

        if($promocode['offer_code']== $request->offer_code) {
            if(!empty($promocode))
            {
                return response()->json(['status'=>1,'message'=>'Promocode has been applied','data'=>$promocode],200);
            }
        } else {
            return response()->json(['status'=>0,'message'=>'You applied wrong Promocode'],200);
        }
    }
}
