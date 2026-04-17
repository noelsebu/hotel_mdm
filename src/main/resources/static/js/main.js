// HotelMDM - main.js
// Auto-dismiss flash alerts after 5 seconds
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.alert.alert-success, .alert.alert-info').forEach(function (alert) {
        setTimeout(function () {
            var bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        }, 5000);
    });
});
