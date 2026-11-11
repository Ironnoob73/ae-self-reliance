---
navigation:
  parent: 0_index.md
  title: 能源接口
  icon: ae_crystal_industries:energy_interface
  position: 230
item_ids:
  - ae_crystal_industries:energy_interface
  - ae_crystal_industries:energy_interface_part
---

<Row gap="1">
    <BlockImage id="energy_interface" scale="2" />
    <ItemImage id="energy_interface_part" scale="2" />
</Row>

# 能源接口

**能源接口** 是一种将 ME 网络内的 AE 能源转化为 FE 的方块。

默认情况下将每 4AE 转化为 1FE。

## 设置

可以设定转化效率和 FE 存储上限。调整存储上限时超出上限的 FE 将会被销毁！

## 优先级

网络内只有优先级最高的能源接口能够工作，直到其 FE 储存充满或是被红石控制关闭。

## 升级

每张<ItemLink id="ae2:energy_card" />每 FE 减少 1AE 消耗，最多降低至 1AE/FE。

可以插入<ItemLink id="ae2:redstone_card" />进行红石控制。