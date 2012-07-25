<style type="text/css">
div.portlet
{
  height:400px;
}
</style> 
<div class="columns-3" id="main-content" role="main">
		<table class="portlet-layout" style="">
		<tr>
			<th style="width:33%"></th>
			<th style="width:33%"></th>
			<th style="width:33%"></th>
		</tr>
		<tr>
			<td class="aui-w33 portlet-column portlet-column-first" id="column-1">
				$processor.processColumn("column-1", "portlet-column-content portlet-column-content-first")
			</td>
			<td class="aui-w33 portlet-column" id="column-2">
				$processor.processColumn("column-2", "portlet-column-content")
			</td>
			<td class="aui-w33 portlet-column portlet-column-last" id="column-3">
				$processor.processColumn("column-3", "portlet-column-content portlet-column-content-last")
			</td>
		</tr>
		</table>
</div>
