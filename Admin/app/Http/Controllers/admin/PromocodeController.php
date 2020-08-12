<?php

namespace App\Http\Controllers\admin;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Promocode;
use Validator;
class PromocodeController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $getpromocode = Promocode::all();
        return view('promocode',compact('getpromocode'));
    }

    public function list()
    {
        $getpromocode = Promocode::all();
        return view('theme.promocodetable',compact('getpromocode'));
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $s
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $validation = Validator::make($request->all(),[
          'offer_name' => 'required|unique:promocode',
          'offer_code' => 'required|unique:promocode',
          'offer_amount' => 'required',
          'description' => 'required',
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
            $promocode = new Promocode;
            $promocode->offer_name =$request->offer_name;
            $promocode->offer_code =$request->offer_code;
            $promocode->offer_amount =$request->offer_amount;
            $promocode->description =$request->description;
            $promocode->save();
            $success_output = 'Promocode Added Successfully!';
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        echo json_encode($output);
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show(Request $request)
    {
        $getpromocode = Promocode::where('id',$request->id)->first();
        return response()->json(['ResponseCode' => 1, 'ResponseText' => 'Promocode fetch successfully', 'ResponseData' => $getpromocode], 200);
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit(Request $req)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request)
    {

        $validation = Validator::make($request->all(),[
          'getoffer_name' => 'required|unique:promocode,offer_name,' . $request->id,
          'getoffer_code' => 'required|unique:promocode,offer_name,' . $request->id,
          'getoffer_amount' => 'required',
          'get_description' => 'required',
        ]);

        $error_array = array();
        $success_output = '';
        if ($validation->fails())
        {
            foreach($validation->messages()->getMessages() as $field_name => $messages)
            {
                $error_array[] = $messages;
            }
            // dd($error_array);
        }
        else
        {
            $promocode = new Promocode;
            $promocode->exists = true;
            $promocode->id = $request->id;
            $promocode->offer_name =$request->getoffer_name;
            $promocode->offer_code =$request->getoffer_code;
            $promocode->offer_amount =$request->getoffer_amount;
            $promocode->description =$request->get_description;
            $promocode->save();           

            $success_output = 'Promocode updated Successfully!';
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        echo json_encode($output);
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy(Request $request)
    {
        $promocode=Promocode::where('id', $request->id)->delete();
        if ($promocode) {
            return 1;
        } else {
            return 0;
        }
    }
}
