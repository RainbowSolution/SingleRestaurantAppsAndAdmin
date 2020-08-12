<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Mail;
use App\User;
use Validator;

class UserController extends Controller
{
    public function register(Request $request )
    {
        if($request->email == ""){
            return response()->json(["status"=>0,"message"=>"Email ID is required"],400);
        }
        if($request->password == ""){
            return response()->json(["status"=>0,"message"=>"Password is required"],400);
        }
        if($request->name == ""){
            return response()->json(["status"=>0,"message"=>"Name is required"],400);
        }
        if($request->mobile == ""){
            return response()->json(["status"=>0,"message"=>"Mobile is required"],400);
        }

        $checkemail=User::where('email',$request['email'])->first();
        
        if(!empty($checkemail))
        {
            return response()->json(['status'=>0,'message'=>'Email ID already exist.'],400);
        }

        $checkmobile=User::where('mobile',$request['mobile'])->first();
        
        if(!empty($checkmobile))
        {
            return response()->json(['status'=>0,'message'=>'Mobile number already exist.'],400);
        }

        $data['name']=$request->get('name');
        $data['mobile']=$request->get('mobile');
        $data['email']=$request->get('email');
        $data['profile_image']='unknown.png';
        $data['password']=Hash::make($request->get('password'));
        $data['type']='2';
        // dd($data);
        $user=User::create($data);
        if($user)
        {
            $arrayName = array(
                'id' => $user->id
            );
            // $data['id']=$user->id;
            return response()->json(['status'=>1,'message'=>'Registration Successful','data'=>$arrayName],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
        }
    }

    public function login(Request $request )
    {
        if($request->email == ""){
            return response()->json(["status"=>0,"message"=>"Email id is required"],400);
        }
        if($request->password == ""){
            return response()->json(["status"=>0,"message"=>"Password is required"],400);
        }
        
        $login=User::where('email',$request['email'])->where('type','=','2')->first();

        if(!empty($login))
        {
            if(Hash::check($request->get('password'),$login->password))
            {
                $arrayName = array(
                    'id' => $login->id,
                    'name' => $login->name,
                    'mobile' => $login->mobile,
                    'email' => $login->email,
                );
                // $login->fcm_token = '';
                $data=array('user'=>$arrayName);
                $status=1;
                $message='Login Successful';

                $data_token['token'] = $request['token'];
                $update=User::where('email',$request['email'])->update($data_token);

                return response()->json(['status'=>$status,'message'=>$message,'data'=>$arrayName],200);
            }
            else
            {
                $status=0;
                $message='Password is incorrect';
                return response()->json(['status'=>$status,'message'=>$message],422);
            }
        }
        else
        {
            $status=0;
            $message='Email is incorrect';
            $data="";
            return response()->json(['status'=>$status,'message'=>$message],422);
        }
        
       
        return response()->json(['status'=>$status,'message'=>$message,'data'=>$data],200);
    }

    public function getprofile(Request $request )
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User ID is required"],400);
        }

        $users = User::where('id',$request['user_id'])->get()->first();

        if ($users->mobile == "") {
            $mobile = "";
        } else {
            $mobile = $users->mobile;
        }

        $arrayName = array(
            'id' => $users->id,
            'name' => $users->name,
            'mobile' => $mobile,
            'email' => $users->email,
            'profile_image' => url('/public/images/profile/'.$users->profile_image)
        );


        if(!empty($arrayName))
        {
            return response()->json(['status'=>1,'message'=>'Profile data','data'=>$arrayName],200);
        } else {
            $status=0;
            $message='No User found';
            $data="";
            return response()->json(['status'=>$status,'message'=>$message],422);
        }

        return response()->json(['status'=>$status,'message'=>$message,'data'=>$data],200);
    }

    public function editprofile(Request $request )
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User ID is required"],400);
        }
        if($request->name == ""){
            return response()->json(["status"=>0,"message"=>"Name is required"],400);
        }

        $user = new User;
        $user->exists = true;
        $user->id = $request->user_id;

        if(isset($request->image)){
            if($request->hasFile('image')){
                $image = $request->file('image');
                $image = 'profile-' . uniqid() . '.' . $request->image->getClientOriginalExtension();
                $request->image->move('public/images/profile', $image);
                $user->profile_image=$image;
            }            
        }
        $user->name =$request->name;
        $user->save();

        if($user)
        {
            return response()->json(['status'=>1,'message'=>'Profile has been updated'],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
        }
    }

    public function changepassword(Request $request)
    {
        if($request->user_id == ""){
            return response()->json(["status"=>0,"message"=>"User is required"],400);
        }
        if($request->old_password == ""){
            return response()->json(["status"=>0,"message"=>"Old Password is required"],400);
        }
        if($request->new_password == ""){
            return response()->json(["status"=>0,"message"=>"New Password is required"],400);
        }
        if($request['old_password']==$request['new_password'])
        {
            return response()->json(['status'=>0,'message'=>'Old and new password must be different'],400);
        }
        $check_user=User::where('id',$request['user_id'])->get()->first();
        if(Hash::check($request['old_password'],$check_user->password))
        {
            $data['password']=Hash::make($request['new_password']);
            $update=User::where('id',$request['user_id'])->update($data);
            return response()->json(['status'=>1,'message'=>'Password Updated'],200);
        }
        else{
            return response()->json(['status'=>0,'message'=>'Incorrect Password'],400);
        }
    }

    public function forgotPassword(Request $request)
    {
        if($request->email == ""){
            return response()->json(["status"=>0,"message"=>"Email id is required"],400);
        }

        $checklogin=User::where('email',$request['email'])->first();
        
        if(empty($checklogin))
        {
            return response()->json(['status'=>0,'message'=>'Email does not exist'],400);
        }
        else {
            // dd($checklogin->facebook_id);
            if ($checklogin->google_id != "" OR $checklogin->facebook_id != "") {
                return response()->json(['status'=>2,'message'=>'Your account has been registered with social media'],400);
            } else {
                $password = BaseFunction::random_code(8);
                $newpassword['password'] = Hash::make($password);
                $update = User::where('email', $request['email'])->update($newpassword);

                $title='Password Reset';
                $email=$checklogin->email;
                $data=['title'=>$title,'email'=>$email,'password'=>$password];
                try {

                    Mail::send('Email.email',$data,function($message)use($data){
                        $message->from('ramanisiddharth@gmail.com')->subject($data['title']);
                        $message->to($data['email']);
                    } );
                    return response()->json(['status'=>1,'message'=>'New Password Sent to your email address'],201);
                } catch (\Swift_TransportException $e) { 
                    return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
                }
            }
        }

    }
}
