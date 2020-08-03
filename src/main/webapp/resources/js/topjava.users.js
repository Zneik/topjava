// $(document).ready(function () {
function enable(checkbox, userId) {
    let enabled = checkbox.is(":checked")
    $.ajax({
        url: 'admin/users/' + userId + '/enable',
        type: 'POST',
        data: 'enabled=' + enabled
    }).done(function () {
        checkbox.closest('tr')
            .attr('data-userEnabled', enabled);
        successNoty(enabled ? "Enabled" : "Disabled");
    }).fail(function () {
        $(checkbox).prop("checked", !enabled);
    });
}

function updateTableUsers() {
    $.get("admin/users/", updateTableData);
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
            }),
            updateTable: updateTableUsers
        }
    );
});