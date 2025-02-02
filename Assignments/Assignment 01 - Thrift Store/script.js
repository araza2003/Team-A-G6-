// Basic JavaScript for interactions

// Wait for the DOM to load
document.addEventListener('DOMContentLoaded', function() {
  // Example: Handle "Read More" button in About Us section
  const aboutUsBtn = document.querySelector('.about-us button');
  aboutUsBtn.addEventListener('click', function() {
    alert('Read more about us functionality coming soon!');
  });

  // Example: Handle "Subscribe" in Newsletter section
  const newsletterForm = document.querySelector('.newsletter form');
  newsletterForm.addEventListener('submit', function(e) {
    e.preventDefault();
    const emailInput = this.querySelector('input[type="email"]');
    if(emailInput.value) {
      alert(`Subscribed with ${emailInput.value}`);
      emailInput.value = '';
    }
  });

  // Example: Footer newsletter subscription
  const footerForm = document.querySelector('.footer-section form');
  footerForm.addEventListener('submit', function(e) {
    e.preventDefault();
    const emailInput = this.querySelector('input[type="email"]');
    if(emailInput.value) {
      alert(`Footer subscribed with ${emailInput.value}`);
      emailInput.value = '';
    }
  });
});
