# Keep the App Startup Initializer
-keep class com.elianfabian.activity_provider.ActivityProviderInitializer { *; }

# Keep the ActivityProvider object if needed (though it's usually kept by usages)
-keep class com.elianfabian.activity_provider.ActivityProvider { *; }
