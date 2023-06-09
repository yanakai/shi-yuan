<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="服务项目名称" prop="additionalName">
        <el-input
          v-model="queryParams.additionalName"
          placeholder="请输入服务项目名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="服务项目类型" prop="dataType">
        <el-select v-model="queryParams.dataType" placeholder="请选择服务项目类型" clearable>
          <el-option
            v-for="dict in dict.type.b_additional_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['business:additional:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['business:additional:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="additionalList" @selection-change="handleSelectionChange">
       <el-table-column label="服务项目类型" align="center" prop="dataType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.b_additional_type" :value="scope.row.dataType"/>
        </template>
      </el-table-column>
      <el-table-column label="服务项目名称" align="center" prop="additionalName" />
      <el-table-column label="服务项目金额" align="center" prop="additionalAmount" />
       <el-table-column label="时长（分钟）" align="center" prop="duration" />
      <el-table-column label="排序" align="center" prop="orderNum" />
      <el-table-column label="备注" align="center" prop="remarks" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="small"
            type="primary"
            round
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['business:additional:edit']"
          >修改</el-button>
          <el-button
            size="small"
            type="danger"
            round
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['business:additional:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改附加项单次金额对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="750px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
            <el-col :span="24">
              <el-form-item label="服务项目名称" prop="additionalName">
                <el-input v-model="form.additionalName" placeholder="请输入服务项目名称" />
              </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
              <el-form-item label="服务项目类型" prop="dataType">
                  <el-select v-model="form.dataType" placeholder="请选择服务项目类型">
                    <el-option
                      v-for="dict in dict.type.b_additional_type"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    ></el-option>
                  </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="服务项目金额" prop="additionalAmount">
                <el-input v-model="form.additionalAmount" placeholder="请输入服务项目金额" />
              </el-form-item>
            </el-col>
        </el-row>
        <el-row>
           <el-col :span="12">
              <el-form-item label="时长" prop="duration">
                <el-input-number v-model="form.duration" controls-position="right"  />分钟
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="排序" prop="orderNum">
                <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
              </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="24">
              <el-form-item label="备注" prop="remarks">
                <el-input v-model="form.remarks" type="textarea" placeholder="请输入备注" />
              </el-form-item>
            </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAdditional, getAdditional, delAdditional, addAdditional, updateAdditional } from "@/api/business/additional";

export default {
  name: "Additional",
  dicts: ['b_additional_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 附加项单次金额表格数据
      additionalList: [],
      // 弹出层标题
      title: "",
      // 服务项目对应时长
      durationMin:10,
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        additionalName: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        additionalName: [
          { required: true, message: "服务项目名称不能为空", trigger: "blur" },
          { min: 2, max: 20, message: '服务项目名称长度必须介于 2 和 30 之间', trigger: 'blur' }
        ],
        dataType: [
          { required: true, message: "服务项目类型不能为空", trigger: "blur" }
        ],
        additionalAmount: [
          { required: true, message: "服务项目金额不能为空", trigger: "blur" },
          {
            pattern: /^\d+(\.\d{0,2})?$/,
            message: "只能输入数字或者两位小数",
            trigger: "blur"
          }
        ],
      }
      
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询附加项单次金额列表 */
    getList() {
      this.loading = true;
      listAdditional(this.queryParams).then(response => {
        this.additionalList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        additionalId: null,
        additionalName: null,
        additionalAmount: null,
        orderNum: null,
        createTime: null,
        createBy: null,
        updateTime: null,
        updateBy: null,
        remarks: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.additionalId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加服务项目";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const additionalId = row.additionalId || this.ids
      getAdditional(additionalId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改服务项目";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.additionalId != null) {
            updateAdditional(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAdditional(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const additionalIds = row.additionalId || this.ids;
      this.$modal.confirm('是否确认删除服务项目编号为"' + additionalIds + '"的数据项？').then(function() {
        return delAdditional(additionalIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('business/additional/export', {
        ...this.queryParams
      }, `additional_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
