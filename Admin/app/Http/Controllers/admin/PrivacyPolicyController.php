<?php

namespace App\Http\Controllers\admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\PrivacyPolicy;
use Validator;

class PrivacyPolicyController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $getprivacypolicy = PrivacyPolicy::where('id','1')->first();
        return view('privacypolicy',compact('getprivacypolicy'));
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
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        //
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
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
              'privacypolicy' => 'required',
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
                $privacypolicy = new PrivacyPolicy;
                $privacypolicy->exists = true;
                $privacypolicy->id = '1';
                $privacypolicy->privacypolicy_content =$request->privacypolicy;
                $privacypolicy->save();           

                $success_output = 'Privacy Policy content has been updated Successfully!';
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
    public function destroy($id)
    {
        //
    }
}
