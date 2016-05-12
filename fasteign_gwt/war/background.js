chrome.app.runtime.onLaunched.addListener(function() {
  chrome.app.window.create('Fasteignaverd.html', {
    'width': 400,
    'height': 500
  });
});
