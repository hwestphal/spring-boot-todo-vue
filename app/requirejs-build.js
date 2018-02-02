({
  appDir : '${basedir}/src/main/resources/static',
  dir : '${target}/classes/static',
  optimizeCss : 'standard',
  writeBuildTxt : false,

  baseUrl : 'js',
  modules : [ {
    name : 'main'
  } ],

  paths : {
    vue : '${webjars}/vue/dist/vue.min',
    awesomplete : '${webjars}/awesomplete/awesomplete'
  },
  shim : {
    awesomplete : {
      exports : 'Awesomplete'
    }
  }
})
