<?php

namespace App\Http\Middleware;

use Closure;
use Auth;

class AdminAuth
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {
        // 
       if (Auth::user() &&  !Auth::user()->hasRole('user') ) {
            return $next($request);
        }
        Auth::logout();
        return redirect('admin');
    }
}
