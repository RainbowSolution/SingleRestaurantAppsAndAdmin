<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Ratting;
use Illuminate\Support\Facades\DB;
use Validator;

class RattingController extends Controller
{
    public function ratting(Request $request)
    {
      	if($request->user_id == ""){
          	return response()->json(["status"=>0,"message"=>"User is required"],400);
      	}

      	$data=Ratting::where('ratting.user_id',$request['user_id'])
      	->get()
	    ->first();
  		try {

		    if($data=="") {
		    	$ratting = new Ratting;
		    	$ratting->user_id =$request->user_id;
		    	$ratting->ratting =$request->ratting;
		    	$ratting->comment =$request->comment;
		    	$ratting->save();

		    	return response()->json(['status'=>1,'message'=>'Your Review has been submitted'],200);
	        } else {
	            return response()->json(['status'=>0,'message'=>'Already Submitted.'],400);
	        }
  		    
  		} catch (\Exception $e){

  		    return response()->json(['status'=>0,'message'=>'Something went wrong'],400);
  		}
   	}


    public function rattinglist(Request $request)
    {
		$rattingdata=Ratting::select('ratting.ratting','ratting.comment','ratting.created_at','users.name')
		->join('users','ratting.user_id','=','users.id')->get()->toArray();

		foreach ($rattingdata as $value) {
			$data[] = array(
			    "ratting" => $value['ratting'],
			    "comment" => $value['comment'],
			    "name" => $value['name'],
			    "created_at" => date('d M Y', strtotime($value['created_at']))
			);
		}

        if(!empty($data))
        {
        	return response()->json(['status'=>1,'message'=>'Ratting List','data'=>$data],200);
        }
        else
        {
            return response()->json(['status'=>0,'message'=>'No data found'],200);
        }
    }
}
