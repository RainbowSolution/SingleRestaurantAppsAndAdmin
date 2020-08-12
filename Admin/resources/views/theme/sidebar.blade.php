<!--**********************************
    Sidebar start
***********************************-->
<div class="nk-sidebar">           
    <div class="nk-nav-scroll">
        <ul class="metismenu" id="menu">
            <li class="nav-label">Dashboard</li>
            <li>
                <a href="{{URL::to('/admin/home')}}" aria-expanded="false">
                    <i class="icon-speedometer menu-icon"></i><span class="nav-text">Dashboard</span>
                </a>
            </li>
            <li>
                <a href="{{URL::to('/admin/category')}}" aria-expanded="false">
                    <i class="icon-menu menu-icon"></i><span class="nav-text">Category</span>
                </a>
            </li>
            <li>
                <a href="{{URL::to('/admin/item')}}" aria-expanded="false">
                    <i class="fa fa-plus"></i><span class="nav-text">Items</span>
                </a>
            </li>
            <li>
                <a href="{{URL::to('/admin/users')}}" aria-expanded="false">
                    <i class="fa fa-users"></i><span class="nav-text">Users</span>
                </a>
            </li>
            <li>
                <a href="{{URL::to('/admin/orders')}}" aria-expanded="false">
                    <i class="fa fa-shopping-cart"></i><span class="nav-text">Orders</span>
                </a>
            </li>
            <li>
                <a href="{{URL::to('/admin/reviews')}}" aria-expanded="false">
                    <i class="fa fa-star"></i><span class="nav-text">Reviews</span>
                </a>
            </li>
            <li>
                <a href="{{URL::to('/admin/promocode')}}" aria-expanded="false">
                    <i class="fa fa-tag"></i><span class="nav-text">Promocode</span>
                </a>
            </li>
            <li>
                <a class="has-arrow" href="javascript:void()" aria-expanded="false">
                    <i class="icon-note menu-icon"></i><span class="nav-text">CMS Pages</span>
                </a>
                <ul aria-expanded="false">
                    <li><a href="{{URL::to('/admin/privacypolicy')}}">Privacy Policy</a></li>
                </ul>
            </li>
        </ul>
    </div>
</div>
<!--**********************************
    Sidebar end
***********************************-->