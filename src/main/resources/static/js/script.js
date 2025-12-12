$(function(){

// User Register validation

	var $userRegister=$("#userRegister");

	$userRegister.validate({
		
		rules:{
			name:{
				required:true,
				lettersonly:true
			}
			,
			email: {
				required: true,
				space: true,
				email: true
			},
			mobileNumber: {
				required: true,
				space: true,
				numericOnly: true,
				minlength: 10,
				maxlength: 12

			},
			password: {
				required: true,
				space: true

			},
			confirmpassword: {
				required: true,
				space: true,
				equalTo: '#pass'

			},
			address: {
				required: true,
				all: true

			},

			city: {
				required: true,

			},
			state: {
				required: true,


			},
			pincode: {
				required: true,
				space: true,
				numericOnly: true

			}, img: {
				required: true,
			}
			
		},
        messages: {
            name: {
                required: 'Tên bắt buộc nhập',
                lettersonly: 'Tên không hợp lệ, chỉ được chữ cái'
            },
            email: {
                required: 'Email bắt buộc nhập',
                space: 'Không được chứa khoảng trắng',
                email: 'Email không hợp lệ'
            },
            mobileNumber: {
                required: 'Số điện thoại bắt buộc nhập',
                space: 'Không được chứa khoảng trắng',
                numericOnly: 'Số điện thoại không hợp lệ',
                minlength: 'Tối thiểu 10 chữ số',
                maxlength: 'Tối đa 12 chữ số'
            },
            password: {
                required: 'Mật khẩu bắt buộc nhập',
                space: 'Không được chứa khoảng trắng'
            },
            confirmpassword: {
                required: 'Xác nhận mật khẩu bắt buộc nhập',
                space: 'Không được chứa khoảng trắng',
                equalTo: 'Mật khẩu xác nhận không khớp'
            },
            address: {
                required: 'Địa chỉ bắt buộc nhập',
                all: 'Địa chỉ không hợp lệ'
            },
            city: {
                required: 'Thành phố bắt buộc nhập',
                space: 'Không được chứa khoảng trắng ở đầu/cuối'
            },
            state: {
                required: 'Tỉnh/Thành phố bắt buộc nhập',
                space: 'Không được chứa khoảng trắng ở đầu/cuối'
            },
            pincode: {
                required: 'Mã bưu điện bắt buộc nhập',
                space: 'Không được chứa khoảng trắng',
                numericOnly: 'Mã bưu điện không hợp lệ'
            },
            img: {
                required: 'Ảnh bắt buộc chọn'
            }
        }

	})
	
	
// Orders Validation

var $orders=$("#orders");

$orders.validate({
		rules:{
			firstName:{
				required:true,
				lettersonly:true
			},
			lastName:{
				required:true,
				lettersonly:true
			}
			,
			email: {
				required: true,
				space: true,
				email: true
			},
			mobileNo: {
				required: true,
				space: true,
				numericOnly: true,
				minlength: 10,
				maxlength: 12

			},
			address: {
				required: true,
				all: true

			},

			city: {
				required: true,


			},
			state: {
				required: true,


			},
			pincode: {
				required: true,
				space: true,
				numericOnly: true

			},
			paymentType:{
			required: true
			}
		},
        messages: {
            firstName: {
                required: 'Họ bắt buộc nhập',
                lettersonly: 'Tên không hợp lệ, chỉ được chữ cái'
            },
            lastName: {
                required: 'Tên đệm / tên bắt buộc nhập',
                lettersonly: 'Tên không hợp lệ, chỉ được chữ cái'
            },
            email: {
                required: 'Email bắt buộc nhập',
                space: 'Không được chứa khoảng trắng',
                email: 'Email không hợp lệ'
            },
            mobileNo: {
                required: 'Số điện thoại bắt buộc nhập',
                space: 'Không được chứa khoảng trắng',
                numericOnly: 'Số điện thoại không hợp lệ',
                minlength: 'Tối thiểu 10 chữ số',
                maxlength: 'Tối đa 12 chữ số'
            },
            address: {
                required: 'Địa chỉ bắt buộc nhập',
                all: 'Địa chỉ không hợp lệ'
            },
            city: {
                required: 'Thành phố bắt buộc nhập',

            },
            state: {
                required: 'Tỉnh/Thành phố bắt buộc nhập',

            },
            pincode: {
                required: 'Mã bưu điện bắt buộc nhập',
                space: 'Không được chứa khoảng trắng',
                numericOnly: 'Mã bưu điện không hợp lệ'
            },
            paymentType: {
                required: 'Vui lòng chọn phương thức thanh toán'
            }
        }

})

// Reset Password Validation

var $resetPassword=$("#resetPassword");

$resetPassword.validate({
		
		rules:{
			password: {
				required: true,
				space: true

			},
			confirmPassword: {
				required: true,
				space: true,
				equalTo: '#pass'

			}
		},
		messages: {
            password: {
                required: 'Mật khẩu bắt buộc nhập',
                space: 'Không được chứa khoảng trắng'
            },
            confirmpassword: {
                required: 'Xác nhận mật khẩu bắt buộc nhập',
                space: 'Không được chứa khoảng trắng',
                equalTo: 'Mật khẩu xác nhận không khớp'
            }
        }

})


var $userProfileUpdate=$("#userProfileUpdate");
$userProfileUpdate.validate({

})
	
	
	
})



jQuery.validator.addMethod('lettersonly', function(value, element) {
		return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
	});
	
		jQuery.validator.addMethod('space', function(value, element) {
		return /^[^-\s]+$/.test(value);
	});

	jQuery.validator.addMethod('all', function(value, element) {
		return /^[^-\s][a-zA-Z0-9_,.\s-]+$/.test(value);
	});


	jQuery.validator.addMethod('numericOnly', function(value, element) {
		return /^[0-9]+$/.test(value);
	});

	// ------------------
	// Profile Page Validation
	// ------------------
	$(function(){
		// Update Profile form (match by action attribute)
		var $updateProfile = $('form[action="/user/update-profile"]');
		if ($updateProfile.length) {
			$updateProfile.validate({
				rules: {
					name: {
						required: true,
						lettersonly: true
					},
					mobileNumber: {
						required: true,
						space: true,
						numericOnly: true,
						minlength: 10,
						maxlength: 12
					},
					address: {
						required: true,
						all: true
					},
					city: {
						required: true
					},
					state: {
						required: true
					},
					pincode: {
						required: true,
						space: true,
						numericOnly: true
					},
					img: {
						required: false
					}
				},
				messages: {
					name: {
						required: 'Tên bắt buộc nhập',
						lettersonly: 'Tên không hợp lệ, chỉ được chữ cái'
					},
					mobileNumber: {
						required: 'Số điện thoại bắt buộc nhập',
						space: 'Không được chứa khoảng trắng',
						numericOnly: 'Số điện thoại không hợp lệ',
						minlength: 'Tối thiểu 10 chữ số',
						maxlength: 'Tối đa 12 chữ số'
					},
					address: {
						required: 'Địa chỉ bắt buộc nhập',
						all: 'Địa chỉ không hợp lệ'
					},
					city: {
						required: 'Thành phố bắt buộc nhập'
					},
					state: {
						required: 'Tỉnh/Thành phố bắt buộc nhập'
					},
					pincode: {
						required: 'Mã bưu điện bắt buộc nhập',
						space: 'Không được chứa khoảng trắng',
						numericOnly: 'Mã bưu điện không hợp lệ'
					}
				}
			});
		}

		// Change Password form (match by action attribute)
		var $changePwd = $('form[action="/user/change-password"]');
		if ($changePwd.length) {
			$changePwd.validate({
				rules: {
					currentPassword: {
						required: true,
						space: true
					},
					newPassword: {
						required: true,
						space: true,
						minlength: 6
					},
					confirmPassword: {
						required: true,
						space: true,
						equalTo: function(){ return $changePwd.find('input[name="newPassword"]'); }
					}
				},
				messages: {
					currentPassword: {
						required: 'Mật khẩu hiện tại bắt buộc nhập',
						space: 'Không được chứa khoảng trắng'
					},
					newPassword: {
						required: 'Mật khẩu mới bắt buộc nhập',
						space: 'Không được chứa khoảng trắng',
						minlength: 'Mật khẩu mới phải ít nhất 6 ký tự'
					},
					confirmPassword: {
						required: 'Xác nhận mật khẩu bắt buộc nhập',
						space: 'Không được chứa khoảng trắng',
						equalTo: 'Mật khẩu xác nhận không khớp'
					}
				}
			});
		}
	});
