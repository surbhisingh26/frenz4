$(".right-side-toggle-chat").click(function(){$(".right-sidebar-chat").slideDown(50),$(".right-sidebar-chat").toggleClass("shw-rside-chat")})

$(document).ready(function() {
               
                    longPolling.stop = false;
                    longPolling.poll();
                
                $("#stopPolling").click(function(event) {
                    event.preventDefault();
                    longPolling.stop = true;
                });
            });
 
            var longPolling = {
                stop: false,
                poll: function() {
                    if (longPolling.stop === true) {
                        console.log("Stopping the long polling process");
                        return;
                    }
                    $.ajax(
                            {
                                type: 'POST',
                                url: "checkmessage",
                                data: {},
                                //contentType: "application/json; charset=utf-8",
                                dataType: "json",
								
                                // crossDomain: true,
                                //timeout: 50000
                            }
                    ).done(function(data, textStatus, jqXHR) {
					console.log("data ",data);
					//console.log("jqXHR ",jqXHR);
					//alert(response.chatList);
					if(data.chatList.length > 0){
					$.each(data.chatList,function(k,v){
						console.log('gggggggggggggggg', v.sentAt);
						//Jun 2, 2018 4:39:16 PM
					$('.textlist').append('<li class="reverse"><divclass="chat-time">'+moment.utc(v.sentAt, 'MMM d, yyyy hh:mm:ss A').format('hh:mm a')+'</div><div class="chat-content"><h5>'+v.friend[0].name+'</h5><div class="box bg-light-inverse">'+v.text+'</div></div><div class="chat-img"><img src="'+v.friend[0].imagepath+'" alt="user" /></div></li>');

                     });
					}
						
						
                    }).fail(function(jqxhr, textStatus, errorThrown) {
                        console.log("jqxhr.statusText: " + jqxhr.statusText);
                        console.log("status: " + jqxhr.status);
                        console.log("textStatus: " + textStatus); //null, "timeout", "error", "abort", and "parsererror"
                        console.log("errorThrown: " + errorThrown);
                    }).always(longPolling.poll);
                }
            };