<?php

namespace App\Http\Controllers\admin;

use App\Http\Controllers\Controller;
use Auth;
use Illuminate\Http\Request;
use Redirect;
use validate;
use Hash;
use Session;
use App\User;
use App\Category;
use App\Item;
use App\Ratting;
use App\Order;
use App\Promocode;

class AdminController extends Controller {
    public function login() {
        return view('login');
    }

    public function home() {
        $getcategory = Category::all();
        $getitems = Item::all();
        $getreview = Ratting::all();
        $getorders = Order::all();
        $getpromocode = Promocode::all();
        $getusers = User::Where('type','!=','1')->get();
        return view('home',compact('getcategory','getitems','getusers','getreview','getorders','getpromocode'));
    }

    public function changePassword(request $request)
    {
        $validation = \Validator::make($request->all(), [
            'oldpassword'=>'required|min:6',
            'newpassword'=>'required|min:6',
            'confirmpassword'=>'required_with:newpassword|same:newpassword|min:6',
        ],[
            'oldpassword.required'=>'Old Password is required',
            'newpassword.required'=>'New Password is required',
            'confirmpassword.required'=>'Confirm Password is required'
        ]);
         
        $error_array = array();
        $success_output = '';
        if ($validation->fails())
        {
            foreach($validation->messages()->getMessages() as $field_name => $messages)
            {
                $error_array[] = $messages;
            }
        }
        else if($request['oldpassword']==$request['newpassword'])
        {
            $error_array[]='Old and new password must be different';
        }
        else
        {        
            if(\Hash::check($request->oldpassword,Auth::user()->password)){
                $data['password'] = Hash::make($request->newpassword);
                User::where('id', Auth::user()->id)->update($data);
                Session::flash('message', '<div class="alert alert-success"><strong>Success!</strong> Password has been changed.!! </div>');
               
            }else{
                $error_array[]="Old Password is not match.";
            }
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        return json_encode($output);  

    }

    public function settings(request $request)
    {
        $validation = \Validator::make($request->all(), [
            'currency'=>'required',
            'tax'=>'required'
        ]);
        
        $error_array = array();
        $success_output = '';
        if ($validation->fails())
        {
            foreach($validation->messages()->getMessages() as $field_name => $messages)
            {
                $error_array[] = $messages;
            }
        }
        else
        {
            $setting = User::where('id', Auth::user()->id)->update( array('currency'=>$request->currency, 'tax'=>$request->tax) );

            if ($setting) {
                Session::flash('message', '<div class="alert alert-success"><strong>Success!</strong> Data updated.!! </div>');
            } else {
                $error_array[]="Please try again";
            }
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        return json_encode($output);  

    }

    public function logout(Request $request) {
        Auth::logout();
        return Redirect::to('admin/');
    }
}
