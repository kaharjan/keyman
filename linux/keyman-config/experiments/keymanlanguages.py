#!/usr/bin/python3

# Read in local languages json file

# Later check if online
# If so then get it from web api
# Is there a way to check if has changed from cached version and only download if it has?

# Sort languages into regions

# For later restrict to Keyman version 11.0+ and device linux

import datetime
import time
import json
import requests
import requests_cache
import os
from pathlib import Path

def get_api_languages(forceRefresh=False):
	api_url = "https://api.keyman.com/cloud/4.0/languages"
	headers = {'Content-Type': 'application/json',
		'Accept-Encoding': 'gzip, deflate, br'}
	home = str(Path.home())
	cache_dir = os.path.join(home, ".local/share/keyman")
	current_dir = os.getcwd()
	expire_after = datetime.timedelta(days=1)
	if not os.path.isdir(cache_dir):
		os.makedirs(cache_dir)
	os.chdir(cache_dir)
	requests_cache.install_cache(cache_name='keyman_cache', backend='sqlite', expire_after=expire_after)
	now = time.ctime(int(time.time()))
	print("getting languages api")
	response = requests.get(api_url, headers=headers)
	print("Time: {0} / Used Cache: {1}".format(now, response.from_cache))
	os.chdir(current_dir)
	if response.status_code == 200:
	#	return json.loads(response.content.decode('utf-8'))
		return response.json()
	else:
		return None

def parse_languages(data, verbose=False):
	regions = { 1 : {"name" : "Undefined", "languages" : {} }, 2 : {"name" : "Africa", "languages" : {} }, 3 : {"name" : "Asia", "languages" : {} }, 4 :  {"name" : "Europe", "languages" : {} }, 5 :  {"name" : "Unused", "languages" : {} }, 6 :  {"name" : "Americas", "languages" : {} }, 7 : {"name" : "Asia Pacific", "languages" : {} } }
	#options = data['options']
	numlang = 0
	for lang in data['languages']['languages']:
		regions[lang['region']]['languages'][lang['name']] = lang
		numlang = numlang + 1
	print(numlang)
	if verbose:
		for region in regions:
			print("--- Region", str(region), regions[region]['name'], "---")
			for langname in sorted(regions[region]['languages']):
				print(langname)
				for kb in regions[region]['languages'][langname]['keyboards']:
					print("  Keyboard:", kb['name'])
	return regions

def get_regions(verbose=False):
	data = get_api_languages()
	if data:
		return parse_languages(data, verbose)
	else:
		print("Failed to get data, using local file")
		with open("languages", "r") as read_file:
			localdata = json.load(read_file)
			return parse_languages(localdata, verbose)

def main():
	get_regions()

if __name__ == "__main__":
	main()
