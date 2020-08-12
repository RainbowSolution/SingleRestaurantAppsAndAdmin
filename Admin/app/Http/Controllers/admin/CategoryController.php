<?php

namespace App\Http\Controllers\admin;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Category;
use Validator;
class CategoryController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $getcategory = Category::all();
        return view('category',compact('getcategory'));
    }

    public function list()
    {
        $getcategory = Category::all();
        return view('theme.categorytable',compact('getcategory'));
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
          'category_name' => 'required|unique:categories',
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
            $image = 'category-' . uniqid() . '.' . $request->image->getClientOriginalExtension();
            $request->image->move('public/images/category', $image);

            $category = new Category;
            $category->image =$image;
            $category->category_name =$request->category_name;
            $category->save();
            $success_output = 'Category Added Successfully!';
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
        $category = Category::findorFail($request->id);
        $getcategory = Category::where('id',$request->id)->first();
        if($getcategory->image){
            $getcategory->image=url('public/images/category/'.$getcategory->image);
        }
        return response()->json(['ResponseCode' => 1, 'ResponseText' => 'Category fetch successfully', 'ResponseData' => $getcategory], 200);
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
          'category_name' => 'required|unique:categories,category_name,' . $request->id,
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
            $category = new Category;
            $category->exists = true;
            $category->id = $request->id;

            if(isset($request->image)){
                if($request->hasFile('image')){
                    $image = $request->file('image');
                    $image = 'category-' . uniqid() . '.' . $request->image->getClientOriginalExtension();
                    $request->image->move('public/images/category', $image);
                    $category->image=$image;
                }            
            }
            $category->category_name =$request->category_name;
            $category->save();           

            $success_output = 'Category updated Successfully!';
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
        $category=Category::where('id', $request->id)->delete();
        if ($category) {
            return 1;
        } else {
            return 0;
        }
    }
}
