@extends('theme.default')

@section('content')


    <div class="container-fluid mt-3">
        <div class="row">
            <div class="col-lg-3 col-sm-6">
                <div class="card gradient-1">
                    <div class="card-body">
                        <h3 class="card-title text-white">Total Categories</h3>
                        <div class="d-inline-block">
                            <h2 class="text-white">{{count($getcategory)}}</h2>
                        </div>
                        <span class="float-right display-5 opacity-5"><i class="fa fa-list-alt"></i></span>
                    </div>
                </div>
            </div>
            <div class="col-lg-3 col-sm-6">
                <div class="card gradient-2">
                    <div class="card-body">
                        <h3 class="card-title text-white">Total Items</h3>
                        <div class="d-inline-block">
                            <h2 class="text-white">{{count($getitems)}}</h2>
                        </div>
                        <span class="float-right display-5 opacity-5"><i class="fa fa-cutlery"></i></span>
                    </div>
                </div>
            </div>
            <div class="col-lg-3 col-sm-6">
                <div class="card gradient-3">
                    <div class="card-body">
                        <h3 class="card-title text-white">Total Users</h3>
                        <div class="d-inline-block">
                            <h2 class="text-white">{{count($getusers)}}</h2>
                        </div>
                        <span class="float-right display-5 opacity-5"><i class="fa fa-users"></i></span>
                    </div>
                </div>
            </div>
            <div class="col-lg-3 col-sm-6">
                <div class="card gradient-4">
                    <div class="card-body">
                        <h3 class="card-title text-white">Total Orders</h3>
                        <div class="d-inline-block">
                            <h2 class="text-white">{{count($getorders)}}</h2>
                        </div>
                        <span class="float-right display-5 opacity-5"><i class="fa fa-shopping-cart"></i></span>
                    </div>
                </div>
            </div>
            <div class="col-lg-3 col-sm-6">
                <div class="card gradient-6">
                    <div class="card-body">
                        <h3 class="card-title text-white">Total Reviews</h3>
                        <div class="d-inline-block">
                            <h2 class="text-white">{{count($getreview)}}</h2>
                        </div>
                        <span class="float-right display-5 opacity-5"><i class="fa fa-star"></i></span>
                    </div>
                </div>
            </div>
            <div class="col-lg-3 col-sm-6">
                <div class="card gradient-7">
                    <div class="card-body">
                        <h3 class="card-title text-white">Total Promocode</h3>
                        <div class="d-inline-block">
                            <h2 class="text-white">{{count($getpromocode)}}</h2>
                        </div>
                        <span class="float-right display-5 opacity-5"><i class="fa fa-gift"></i></span>
                    </div>
                </div>
            </div>
            <div class="col-lg-3 col-sm-6">
                <div class="card gradient-8">
                    <div class="card-body">
                        <h3 class="card-title text-white">Currency</h3>
                        <div class="d-inline-block">
                            <h2 class="text-white">{{{Auth::user()->currency}}}</h2>
                        </div>
                        <span class="float-right display-5 opacity-5"><i class="fa fa-money"></i></span>
                    </div>
                </div>
            </div>
            <div class="col-lg-3 col-sm-6">
                <div class="card gradient-9">
                    <div class="card-body">
                        <h3 class="card-title text-white">Tax</h3>
                        <div class="d-inline-block">
                            <h2 class="text-white">{{{Auth::user()->tax}}}</h2>
                        </div>
                        <span class="float-right display-5 opacity-5"><i class="fa fa-calculator"></i></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- #/ container -->
@endsection