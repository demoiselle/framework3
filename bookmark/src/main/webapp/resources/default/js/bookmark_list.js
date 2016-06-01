var bookmarkController = {
    selectAll: function (checkObj) {
        $('[id$="bookmark_check"]').prop('checked', checkObj.checked);
    },
}
