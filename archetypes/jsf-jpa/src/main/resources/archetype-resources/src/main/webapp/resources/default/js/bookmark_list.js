var bookmarkController = {
    selectAll: function (checkObj) {
        $('[id$="bookmark_check"]').prop('checked', checkObj.checked);
    },
    
    paginationErrorDialog : function(data) {
        var alertText = "<div id=\"alert_panel\" class=\"alert alert-danger\" role=\"alert\">" +
                        "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\"><span aria-hidden=\"true\">&#215;</span></button>" +
                        "<span id=\"alert_panel_message\">" + data.errorMessage + "</span>" +
                        "</div>";

        $("#alert_container").append(alertText);

        return false;
    },
}
