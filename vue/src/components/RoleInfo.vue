<template>
  <!-- Btn Section -->
  <div style="padding: 10px 0">
    <el-input style="width: 200px" placeholder="Please input name" :suffix-icon="Search" v-model="name" />
    <el-button class="ml-5" type="primary" @click="load">Search</el-button>
    <el-button type="warning" @click="reset">Reset</el-button>
  </div>

  <div>
    <el-button type="primary" @click="addDialogForm = true">
      Add
      <el-icon>
        <CirclePlus />
      </el-icon>
    </el-button>
    <el-button type="danger" @click="deleteBatch">
      DeleteBatch
      <el-icon>
        <Remove />
      </el-icon>
    </el-button>
  </div>

  <!-- Add Pop Form-->
  <el-dialog v-model="addDialogForm" title="Add Form" width="30%" center>
    <el-form label-width="100px" :model="addForm" style="max-width: 460px">
      <el-form-item label="Name">
        <el-input v-model="addForm.name" />
      </el-form-item>
      <el-form-item label="Unique Key">
        <el-input v-model="addForm.flag" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="addDialogForm = false">Cancel</el-button>
        <el-button type="primary" @click="save">
          Confirm
        </el-button>
      </span>
    </template>
  </el-dialog>

  <!-- Edit Pop Form-->
  <el-dialog v-model="editDialogForm" title="Edit Form" width="30%" center>
    <el-form label-width="100px" :model="editForm" style="max-width: 460px">
      <el-form-item label="Name">
        <el-input v-model="editForm.name" />
      </el-form-item>
      <el-form-item label="Unique Key">
        <el-input v-model="editForm.flag" />
      </el-form-item>
      <el-form-item label="Role">
        <div style="width: 100%;">
          <el-tree ref="permissionTreeRef" node-key="id" :data="treeData"
            :props="{ label: 'name', value: 'id', children: 'children' }" show-checkbox default-expand-all />
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="cancelEdit">Cancel</el-button>
        <el-button type="primary" @click="update">
          Confirm
        </el-button>
      </span>
    </template>
  </el-dialog>

  <!-- table -->
  <el-table :data="tableData" style="width: 100%">
    <el-table-column type="selection" width="55" />
    <el-table-column type="index" prop="id" label="ID" width="50" />
    <el-table-column prop="name" label="Name" width="180" />
    <el-table-column prop="flag" label="Unique Key" width="180" />

    <el-table-column label="Option" width="500">
      <template #default="scope">
        <el-button type="primary" @click="assignPermission(scope.row)">Assign Permissions</el-button>
        <el-button type="success" @click="doEdit(scope.row)">Edit</el-button>
        <el-popconfirm confirm-button-text="Yes" cancel-button-text="No" title="Are you sure to delete this?"
          @confirm="confirmDelete(scope.row.id)" @cancel="cancelEvent">
          <template #reference>
            <el-button type="danger">Delete</el-button>
          </template>
        </el-popconfirm>
      </template>
    </el-table-column>
  </el-table>

  <!-- Assign Role -->
  <el-dialog v-model="addDialogTree" title="Form" width="30%" center>
    <el-tree ref="treeRef" :props="defaultProps" :data="menuList" node-key="id" show-checkbox />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="addDialogForm = false">Cancel</el-button>
        <el-button type="primary" @click="save">
          Confirm
        </el-button>
      </span>
    </template>
  </el-dialog>

  <!-- Pagination -->
  <div class="demo-pagination-block">
    <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[5, 10, 20]"
      layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
      @current-change="handleCurrentChange" />
  </div>
</template>
  
<script setup>
import { ref, reactive, onMounted, inject, nextTick } from "vue";
import { ElMessage } from 'element-plus'
import { Search, CirclePlus, Remove } from '@element-plus/icons-vue'


const $axios = inject('$axios')

const treeData = ref([])
const tableData = ref(null)
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(25)
const roleId = ref(null)
const name = ref('')
const addDialogForm = ref(false)
const editDialogForm = ref(false)
const addDialogTree = ref(false)
const addForm = reactive({
  name: '',
  flag: '',
})

const editForm = reactive({
  id: '',
  name: '',
  flag: '',
  permissionIds: []
})

const menuList = ref(null)
const permissionTreeRef = ref()

const defaultProps = {
  children: 'children',
  label: 'menu_name',
}

const treeRef = ref()

onMounted(() => {
  load();
})


const load = () => {
  $axios.get('/api/role/page', { params: { pageNum: pageNum.value, pageSize: pageSize.value, name: name.value } }).then(res => {
    console.log(res);
    if (res.code === '200') {
      total.value = res.result.total
      tableData.value = res.result.data
    }
  }).catch(err => console.log("ERROR" + err))

  $axios.get('/api/permission/tree').then(res => {
    console.log(res);
    treeData.value = res.result
  })
}


const handleSizeChange = val => {
  console.log(`${val} items per page`)
  $axios.get('/api/role/page', { params: { pageNum: pageNum.value, pageSize: val } }).then(res => {
    console.log(res);
    tableData.value = res.result.data
  })
  pageSize.value = val
}

const handleCurrentChange = val => {
  console.log(`current page: ${val}`)
  $axios.get('/api/role/page', { params: { pageNum: val, pageSize: pageSize.value } }).then(res => {
    console.log(res);
    tableData.value = res.result.data
  })
}

const cancelEdit = () => {
  addDialogForm.value = false
}

const doAdd = () => {

}

const doEdit = row => {
  // open editForm
  editDialogForm.value = true
  nextTick(() => {
    // reset the permissionTreeRef
    permissionTreeRef.value.setCheckedKeys([], false)
    // fill the value of editForm
    editForm.id = row.id
    editForm.name = row.name
    editForm.flag = row.flag
    editForm.permissionIds = row.permissionIds

    row.permissionIds.forEach(v => {
      permissionTreeRef.value.setChecked(v, true, false)
    })

  
  })

}

const deleteBatch = () => {

}

const save = () => {
  // 目前被选中的菜单节点
  let checkedKeys = permissionTreeRef.value.getCheckedKeys();
  console.log(checkedKeys);
  // 半选中的菜单节点
  let halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys();
  console.log(halfCheckedKeys);
  checkedKeys.unshify.apply(checkedKeys, halfCheckedKeys);
  console.log(checkedKeys);


  // $axios.post('/api/role', addForm).then(res => {
  //   console.log(res);
  //   if (res.code === '200') {
  //     ElMessage({
  //       message: 'Add Successful',
  //       type: 'success'
  //     })
  //   } else {
  //     ElMessage({
  //       message: 'Add Fail',
  //       type: 'error'
  //     })
  //   }
  //   addDialogForm.value = false
  //   load()
  // }).catch(err => console.log(err))
}

const update = () => {
  // 目前被选中的菜单节点
  let checkedKeys = permissionTreeRef.value.getCheckedKeys();
  // 半选中的菜单节点
  let halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys();
  checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
  editForm.permissionIds = checkedKeys

  $axios.post('/api/role', editForm).then(res => {
    console.log(res);
    if (res.code === '200') {
      ElMessage.success('Edit Successful')
    } else {
      ElMessage.error('Edit Fail')
    }
    editDialogForm.value = false
    load()
  }).catch(err => console.log(err))
}

const confirmDelete = (id) => {
  $axios.post('/api/role/' + id).then(res => {
    console.log(res)
    if (res.code === '200') {
      ElMessage({
        message: 'Delete Successful',
        type: 'success'
      })
      load();
    } else {
      ElMessage({
        message: 'Delete Fail',
        type: 'error'
      })
    }

  }).catch(err => console.log("ERROR" + err))
}

const cancelEvent = () => {
  console.log('cancel!')
}

const reset = () => {
  name.value = ''
  load()
}

const assignPermission = (row) => {
  roleId.value = row.id
  addDialogTree.value = true
  $axios.get('/api/menu').then(res => {
    console.log(res);
    if (res.code === '200') {
      menuList.value = res.result
    }
  }).catch(err => console.log("ERROR" + err))
}

</script>
  
<style></style>