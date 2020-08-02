// $(document).ready(function () {
function setEnabled(checkbox, userId) {
    let enabled = checkbox.is(":checked")
    $.ajax({
        url: 'admin/users/' + userId + '/enable',
        type: 'POST',
        data: 'enabled=' + enabled
    }).done(function () {
        checkbox.closest('tr')
            .attr('data-userEnabled', enabled);
    });
}

$(function () {
    makeEditable({
            ajaxUrl: "admin/users/",
            datatableApi: $("#datatable").DataTable({
                "paging": false,
                "info": true,
                "columns": [
                    {
                        "data": "name"
                    },
                    {
                        "data": "email"
                    },
                    {
                        "data": "roles"
                    },
                    {
                        "data": "enabled"
                    },
                    {
                        "data": "registered"
                    },
                    {
                        "defaultContent": "Edit",
                        "orderable": false
                    },
                    {
                        "defaultContent": "Delete",
                        "orderable": false
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ]
            })
        }
    );
});