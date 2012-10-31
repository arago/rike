(function(ns)
{
  ns.Provider = 
  {
    providers: [],
    
    register: function(name, cb)
    {
      this.providers.push(cb);
    },
    
    show: function(topic)
    {
      $(this.providers).each(function()
      {
        this(topic);
      });
      
      return false;
    }
  }  
})($.namespace('de.arago.help'));