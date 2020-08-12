<?php

namespace App\Http\Controllers\admin;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Category;
use App\Item;
use App\ItemImages;
use App\Ingredients;
use Validator;

class ItemController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index() {
        $getcategory = Category::all();
        $getitem = Item::with('category')->get();

        return view('item', compact('getcategory','getitem'));
    }

    public function list()
    {
        $getitem = Item::with('category')->get();
        return view('theme.itemtable',compact('getitem'));
    }

    public function itemimages($id) {
        $getitemimages = ItemImages::where('item_id', $id)->get();
        $getingredients = Ingredients::where('item_id', $id)->get();
        $itemdetails = Item::join('categories','item.cat_id','=','categories.id')->where('item.id', $id)->get()->first();
        return view('item-images', compact('getitemimages','itemdetails','getingredients'));
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
        // dd($request->cat_id);
        $validation = Validator::make($request->all(),[
          'cat_id' => 'required',
          'item_name' => 'required',
          'price' => 'required',
          'file.*' => 'required'
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
            $item = new Item;
        
            $item->cat_id =$request->cat_id;
            $item->item_name =$request->item_name;
            $item->item_price =$request->price;
            $item->item_description =$request->description;
            $item->delivery_time =$request->delivery_time;
            $item->save();

            if ($request->hasFile('file')) {
                $files = $request->file('file');
                foreach($files as $file){

                    $itemimage = new ItemImages;
                    $image = 'item-' . uniqid() . '.' . $file->getClientOriginalExtension();

                    

                    $file->move('public/images/item', $image);

                    // dd($image);
                    $itemimage->item_id =$item->id;
                    $itemimage->image =$image;
                    $itemimage->save();
                }
            }

            if ($request->hasFile('ingredients')) {
                $ingredients = $request->file('ingredients');
                foreach($ingredients as $file){

                    $itemimage = new Ingredients;
                    $image = 'ingredients-' . uniqid() . '.' . $file->getClientOriginalExtension();

                    

                    $file->move('public/images/ingredients', $image);

                    // dd($image);
                    $itemimage->item_id =$item->id;
                    $itemimage->image =$image;
                    $itemimage->save();
                }
            }

            $success_output = 'Item Added Successfully!';
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        echo json_encode($output);
    }

    public function storeimages(Request $request)
    {
        // dd($request->cat_id);
        $validation = Validator::make($request->all(),[
          'file.*' => 'required'
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

            if ($request->hasFile('file')) {
                $files = $request->file('file');
                foreach($files as $file){

                    $itemimage = new ItemImages;
                    $image = 'item-' . uniqid() . '.' . $file->getClientOriginalExtension();

                    

                    $file->move('public/images/item', $image);

                    // dd($image);
                    $itemimage->item_id =$request->itemid;
                    $itemimage->image =$image;
                    $itemimage->save();
                }
            }

            $success_output = 'Item Added Successfully!';
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        echo json_encode($output);
    }

    public function storeingredientsimages(Request $request)
    {
        // dd($request->cat_id);
        $validation = Validator::make($request->all(),[
          'file.*' => 'required'
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

            if ($request->hasFile('ingredients')) {
                $ingredients = $request->file('ingredients');
                foreach($ingredients as $file){

                    $itemimage = new Ingredients;
                    $image = 'ingredients-' . uniqid() . '.' . $file->getClientOriginalExtension();

                    

                    $file->move('public/images/ingredients', $image);

                    // dd($image);
                    $itemimage->item_id =$request->itemid;
                    $itemimage->image =$image;
                    $itemimage->save();
                }
            }

            $success_output = 'Item Added Successfully!';
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
        $item = Item::findorFail($request->id);
        $getitem = Item::where('id',$request->id)->first();
        return response()->json(['ResponseCode' => 1, 'ResponseText' => 'Item fetch successfully', 'ResponseData' => $getitem], 200);
    }

    public function showimage(Request $request)
    {
        $getitem = ItemImages::where('id',$request->id)->first();
        if($getitem->image){
            $getitem->image=url('public/images/item/'.$getitem->image);
        }
        return response()->json(['ResponseCode' => 1, 'ResponseText' => 'Item Image fetch successfully', 'ResponseData' => $getitem], 200);
    }

    public function showingredients(Request $request)
    {
        $getitem = Ingredients::where('id',$request->id)->first();
        if($getitem->image){
            $getitem->image=url('public/images/ingredients/'.$getitem->image);
        }
        return response()->json(['ResponseCode' => 1, 'ResponseText' => 'Ingredients Image fetch successfully', 'ResponseData' => $getitem], 200);
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
          'getcat_id' => 'required',
          'item_name' => 'required',
          'getprice' => 'required',
          'getdescription' => 'required'
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
            $item = new Item;
            $item->exists = true;
            $item->id = $request->id;

            $item->cat_id =$request->getcat_id;
            $item->item_name =$request->item_name;
            $item->item_price =$request->getprice;
            $item->item_description =$request->getdescription;
            $item->delivery_time =$request->getdelivery_time;
            $item->save();           

            $success_output = 'Item updated Successfully!';
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        echo json_encode($output);
    }

    public function updateimage(Request $request)
    {
        $validation = Validator::make($request->all(),[
          'image' => 'required|image|mimes:jpeg,png,jpg|max:2048'
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
            $itemimage = new ItemImages;
            $itemimage->exists = true;
            $itemimage->id = $request->id;

            if(isset($request->image)){
                if($request->hasFile('image')){
                    $image = $request->file('image');
                    $image = 'item-' . uniqid() . '.' . $request->image->getClientOriginalExtension();
                    $request->image->move('public/images/item', $image);
                    $itemimage->image=$image;
                }            
            }
            $itemimage->save();           

            $success_output = 'Item updated Successfully!';
        }
        $output = array(
            'error'     =>  $error_array,
            'success'   =>  $success_output
        );
        echo json_encode($output);
    }

    public function updateingredients(Request $request)
    {
        $validation = Validator::make($request->all(),[
          'image' => 'required|image|mimes:jpeg,png,jpg|max:2048'
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
            $itemimage = new Ingredients;
            $itemimage->exists = true;
            $itemimage->id = $request->id;

            if(isset($request->image)){
                if($request->hasFile('image')){
                    $image = $request->file('image');
                    $image = 'ingredients-' . uniqid() . '.' . $request->image->getClientOriginalExtension();
                    $request->image->move('public/images/ingredients', $image);
                    $itemimage->image=$image;
                }            
            }
            $itemimage->save();           

            $success_output = 'Ingredients updated Successfully!';
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
        $item=Item::where('id', $request->id)->delete();
        $itemimage=ItemImages::where('item_id', $request->id)->delete();
        $ingredientsimage=Ingredients::where('item_id', $request->id)->delete();
        if ($item) {
            return 1;
        } else {
            return 0;
        }
    }
    public function destroyimage(Request $request)
    {
        $itemimage=ItemImages::where('id', $request->id)->delete();
        if ($itemimage) {
            return 1;
        } else {
            return 0;
        }
    }

    public function destroyingredients(Request $request)
    {
        $ingredientsimage=Ingredients::where('id', $request->id)->delete();
        if ($ingredientsimage) {
            return 1;
        } else {
            return 0;
        }
    }
}
