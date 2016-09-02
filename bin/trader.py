#!/usr/bin/python
#coding:utf-8

import easytrader

user = easytrader.use('yjb',debug=False)
user.prepare('yjb.json')


def balance():
    # 获取资金状况
    b = user.balance
    return b[0]

def position():
    # 获取持仓
    p = user.position
    return p[0]

def entrust():
    # 获取今日委托单
    e = user.entrust
    return e[0]

def buy(code,price,amount):
    # 买入
    ret = user.buy(code, price=price, amount=amount)
    return ret[0]

def sell(code,price,amount):
    # 卖出
    ret = user.sell(code, price=price, amount=amount)
    return ret[0]

def cancel_entrust(entrust_no,stock_code):
    # 撤单
    ret = user.cancel_entrust(entrust_no,stock_code)
    return ret[0]

def deal():
    # 查询当日成交
    ret = user.current_deal
    return ret[0]

def ipo_limit(stock_code):
    # 查询新股申购额度申购上限
    ret = user.get_ipo_limit(stock_code)
    return ret