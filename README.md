# DelegationManager
## About
  This may help you to communicate between Fragments, DialogFragments in Activity.
  Also this may use to communicate any of objects which has one host and don't now about each other.

## Dependency
  Add to your project/build.gradle
  ```
  repositories {
    maven {url "https://jitpack.io"}
  }
  
  dependencies{
    compile 'com.github.SunnyDayDev:DelegationManager:1.1.2'
  }
  ```

## Usage
  Implement DelegationInterface in your Activity:
  ```
  public MainActivity extends Activity implements DelegationInterface{
  
    private DelegationManager mDelegationManager = new DelegationManager();
    
    @Override
    pubilc DelegationManager getDelegationManager(){
      return mDelegationManager;
    }
  }
  ```
  
  Add deleagte from your fragment:
  ```
  public FragmentA extends Fragment implemets OnClickListener{
    
    @Override
    public void onAttach(Context context) {
       super.onAttach(context);
       ((DelegationInterface) context)
               .getDelegationManager()
               .addDelegate(this, OnClickListener.class);
    }

    @Override
    public void onDetach() {
       ((DelegationInterface) getActivity())
               .getDelegationManager()
               .removeDelegate(this, OnClickListener.class);
               //or .removeDelegate(this) to remove all delegates
       super.onDetach();
    }
    
    @Override
    public void onClick(View v){
    {
      //handle callback
    }
  }
  ```
  **Note: You need remove delegate on onDetach() to prevent memmory leaks.**
  
  And get delegate from another fragment/dialog:
  ```
  public FragmentB extends DialogFragment{
  
    @Override
    onCreateView(...){
      //init your dialog
      Button someButton = (Button) view.findViewById(R.id.somebutton);
      
      //Get OnClickListener from delegate
      OnClickListener delegate = ((DelegateInterface)getActivity)
          .getDelegateManager()
          .getDelegateInterface(OnDialogItemClickListener.class);
      someButton.setOnClickListener(delegate);
    }
    
  }
  ```
  
  ** Note: Getters works the same, but only first delegate was triggered. And vice verse if return type of method is Void all delegates for requested type of delegate will triggered.**
  
